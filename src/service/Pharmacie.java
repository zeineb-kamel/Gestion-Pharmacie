package service;

import model.*;
import dao.*;
import exception.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe principale de gestion de la pharmacie.
 * Encapsule les listes de medicaments, clients fideles et appareils medicaux.
 * Utilise les Streams pour les operations de recherche et tri (TP7-8).
 */
public class Pharmacie {
    
    private List<Medicament> listeMedicaments;
    private List<ClientFidele> listeClientsFideles;
    private List<AppareilMedical> listeAppareils;
    private List<Etagere> etageres;
    
    // Maps pour recherche rapide
    private Map<Long, Integer> mapMedicaments;  // numSerie -> nombre d'exemplaires
    private Map<Long, Double> mapClientsFideles; // CIN -> montant total achats
    
    // DAOs pour acces base de donnees
    private MedicamentDAO medicamentDAO;
    private ClientFideleDAO clientDAO;
    private AppareilMedicalDAO appareilDAO;
    
    // ============================================
    // CONSTRUCTEUR
    // ============================================
    
    public Pharmacie() {
        this.listeMedicaments = new ArrayList<>();
        this.listeClientsFideles = new ArrayList<>();
        this.listeAppareils = new ArrayList<>();
        this.etageres = new ArrayList<>();
        this.mapMedicaments = new HashMap<>();
        this.mapClientsFideles = new HashMap<>();
        
        // Initialiser les DAOs
        this.medicamentDAO = new MedicamentDAO();
        this.clientDAO = new ClientFideleDAO();
        this.appareilDAO = new AppareilMedicalDAO();
    }
    
    /**
     * Charge les donnees depuis la base de donnees
     */
    public void chargerDonnees() {
        this.listeMedicaments = medicamentDAO.getAll();
        this.listeClientsFideles = clientDAO.getAll();
        this.listeAppareils = appareilDAO.getAll();
        
        // Remplir les maps
        for (Medicament m : listeMedicaments) {
            mapMedicaments.merge(m.getNumSerie(), 1, Integer::sum);
        }
        for (ClientFidele c : listeClientsFideles) {
            mapClientsFideles.put(c.getCin(), c.getMontantTotalAchats());
        }
    }
    
    // ============================================
    // GESTION DES MEDICAMENTS
    // ============================================
    
    /**
     * Ajoute un medicament a la pharmacie et a la base de donnees
     */
    public void ajouterMedicament(Medicament m) {
        medicamentDAO.ajouter(m);
        listeMedicaments.add(m);
        mapMedicaments.merge(m.getNumSerie(), 1, Integer::sum);
    }
    
    /**
     * Supprime un medicament par son nom
     */
    public boolean supprimerMedicament(String nomMedicament) {
        // Supprimer de la base de donnees
        boolean supprime = medicamentDAO.supprimerParNom(nomMedicament);
        
        if (supprime) {
            // Supprimer de la liste locale
            listeMedicaments.removeIf(m -> m.getNom().equalsIgnoreCase(nomMedicament));
        }
        return supprime;
    }
    
    /**
     * Retourne le nombre de medicaments disponibles
     */
    public int nombreMedicaments() {
        return listeMedicaments.size();
    }
    
    // ============================================
    // ACHAT DE MEDICAMENTS (TP4)
    // ============================================
    
    /**
     * Permet a un client d'acheter un medicament et retourne son prix
     */
    public double achatMedicament(Medicament m, ClientFidele client) throws StockInsuffisantException {
        if (m.getQuantiteStock() <= 0) {
            throw new StockInsuffisantException(m.getNom(), m.getQuantiteStock(), 1);
        }
        
        // Decrementer le stock
        m.setQuantiteStock(m.getQuantiteStock() - 1);
        medicamentDAO.updateStock(m.getCode(), m.getQuantiteStock());
        
        // Prix avec reduction si client fidele
        double prix = m.getTranche(true);
        
        // Mettre a jour le montant des achats du client
        client.ajouterAchat(prix);
        clientDAO.update(client);
        
        return prix;
    }
    
    /**
     * Permet a un client fidele d'acheter un medicament avec reduction possible.
     * Si le montant depasse 100 DT, reduction de 15% et reinitialisation.
     */
    public double achatMedicament(String nomMedicament, long cin) throws StockInsuffisantException, MedicamentNonTrouveException {
        // Trouver le medicament
        Medicament m = listeMedicaments.stream()
                .filter(med -> med.getNom().equalsIgnoreCase(nomMedicament))
                .findFirst()
                .orElseThrow(() -> new MedicamentNonTrouveException(nomMedicament, ""));
        
        // Trouver le client
        ClientFidele client = clientDAO.getByCin(cin);
        if (client == null) {
            throw new DatabaseException("Client avec CIN " + cin + " non trouve");
        }
        
        if (m.getQuantiteStock() <= 0) {
            throw new StockInsuffisantException(m.getNom(), m.getQuantiteStock(), 1);
        }
        
        // Calculer le prix
        double prix = m.getTranche(true);
        
        // Verifier si reduction de 15% applicable
        if (client.getMontantTotalAchats() >= 100) {
            prix = prix * 0.85; // Reduction de 15%
            client.appliquerReductionEtReinitialiser();
        }
        
        // Ajouter au montant
        client.ajouterAchat(prix);
        
        // Decrementer le stock
        m.setQuantiteStock(m.getQuantiteStock() - 1);
        
        // Sauvegarder les modifications
        medicamentDAO.updateStock(m.getCode(), m.getQuantiteStock());
        clientDAO.update(client);
        
        // Mettre a jour la map
        mapClientsFideles.put(cin, client.getMontantTotalAchats());
        
        return prix;
    }
    
    // ============================================
    // METHODES AVEC STREAMS (TP7-8)
    // ============================================
    
    /**
     * Compte le nombre de medicaments dont le nom commence par 'p'
     * et dont le prix est superieur a 2 DT (TP8)
     */
    public long compterMedicamentsCommencantParPEtPrixSup2() {
        return listeMedicaments.stream()
                .filter(m -> m.getNom().toLowerCase().startsWith("p"))
                .filter(m -> m.getPrix() > 2)
                .count();
    }
    
    /**
     * Trie la liste des clients suivant l'ordre alphabetique (TP8)
     */
    public List<ClientFidele> trierClientsParNom() {
        return listeClientsFideles.stream()
                .sorted(Comparator.comparing(ClientFidele::getNom)
                        .thenComparing(ClientFidele::getPrenom))
                .collect(Collectors.toList());
    }
    
    /**
     * Recherche les medicaments par nom avec Stream
     */
    public List<Medicament> rechercherMedicamentsParNom(String nom) {
        return listeMedicaments.stream()
                .filter(m -> m.getNom().toLowerCase().contains(nom.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Recherche les medicaments par categorie avec Stream
     */
    public List<Medicament> rechercherMedicamentsParCategorie(String categorie) {
        return listeMedicaments.stream()
                .filter(m -> m.getTypeMedicament().equalsIgnoreCase(categorie))
                .collect(Collectors.toList());
    }
    
    /**
     * Recherche les medicaments dont le nom commence par certaines lettres
     */
    public List<Medicament> rechercherMedicamentsParPremieresLettres(String lettres) {
        return listeMedicaments.stream()
                .filter(m -> m.getNom().toLowerCase().startsWith(lettres.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Trie les medicaments par prix croissant
     */
    public List<Medicament> trierMedicamentsParPrix() {
        return listeMedicaments.stream()
                .sorted(Comparator.comparingDouble(Medicament::getPrix))
                .collect(Collectors.toList());
    }
    
    /**
     * Trie les medicaments par nom alphabetique
     */
    public List<Medicament> trierMedicamentsParNom() {
        return listeMedicaments.stream()
                .sorted(Comparator.comparing(Medicament::getNom, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule le prix total d'une liste de vendables pour un client fidele (TP5)
     */
    public double calculerPrixFidele(List<Vendable> vendables) {
        return vendables.stream()
                .mapToDouble(v -> v.getTranche(true))
                .sum();
    }
    
    /**
     * Liste uniquement les medicaments homeopathiques (TP5)
     */
    public List<MedicamentHomeopathique> listerMedicamentsHomeo() {
        return listeMedicaments.stream()
                .filter(m -> m instanceof MedicamentHomeopathique)
                .map(m -> (MedicamentHomeopathique) m)
                .collect(Collectors.toList());
    }
    
    /**
     * Liste uniquement les medicaments chimiques
     */
    public List<MedicamentChimique> listerMedicamentsChimiques() {
        return listeMedicaments.stream()
                .filter(m -> m instanceof MedicamentChimique)
                .map(m -> (MedicamentChimique) m)
                .collect(Collectors.toList());
    }
    
    /**
     * Affiche les medicaments a risque qui expirent dans X mois (TP5)
     */
    public List<Medicament> getMedicamentsExpirantDans(int mois) {
        LocalDate dateLimite = LocalDate.now().plusMonths(mois);
        return listeMedicaments.stream()
                .filter(m -> m.getDateExpiration() != null)
                .filter(m -> !m.getDateExpiration().isAfter(dateLimite))
                .sorted(Comparator.comparing(Medicament::getDateExpiration))
                .collect(Collectors.toList());
    }
    
    /**
     * Applique une remise de 30% sur les medicaments qui expirent dans 1 mois
     */
    public void appliquerRemiseMedicamentsExpirants() {
        List<Medicament> expirants = getMedicamentsExpirantDans(1);
        expirants.forEach(m -> {
            m.appliquerRemise(30);
            medicamentDAO.update(m);
        });
    }
    
    // ============================================
    // CALCULS STATISTIQUES AVEC STREAMS
    // ============================================
    
    /**
     * Calcule la moyenne des prix des medicaments
     */
    public double moyennePrixMedicaments() {
        return listeMedicaments.stream()
                .mapToDouble(Medicament::getPrix)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Trouve le medicament le plus cher
     */
    public Optional<Medicament> medicamentLePlusCher() {
        return listeMedicaments.stream()
                .max(Comparator.comparingDouble(Medicament::getPrix));
    }
    
    /**
     * Trouve le medicament le moins cher
     */
    public Optional<Medicament> medicamentLeMoinsCher() {
        return listeMedicaments.stream()
                .min(Comparator.comparingDouble(Medicament::getPrix));
    }
    
    /**
     * Somme des prix de tous les medicaments
     */
    public double sommePrixMedicaments() {
        return listeMedicaments.stream()
                .mapToDouble(Medicament::getPrix)
                .sum();
    }
    
    /**
     * Groupe les medicaments par genre
     */
    public Map<String, List<Medicament>> grouperParGenre() {
        return listeMedicaments.stream()
                .collect(Collectors.groupingBy(Medicament::getGenre));
    }
    
    /**
     * Groupe les medicaments par type (chimique/homeopathique)
     */
    public Map<String, List<Medicament>> grouperParType() {
        return listeMedicaments.stream()
                .collect(Collectors.groupingBy(Medicament::getTypeMedicament));
    }
    
    // ============================================
    // GESTION DES CLIENTS
    // ============================================
    
    public void ajouterClient(ClientFidele client) {
        clientDAO.ajouter(client);
        listeClientsFideles.add(client);
        mapClientsFideles.put(client.getCin(), client.getMontantTotalAchats());
    }
    
    public boolean supprimerClient(long cin) {
        boolean supprime = clientDAO.supprimer(cin);
        if (supprime) {
            listeClientsFideles.removeIf(c -> c.getCin() == cin);
            mapClientsFideles.remove(cin);
        }
        return supprime;
    }
    
    // ============================================
    // GESTION DES APPAREILS MEDICAUX
    // ============================================
    
    public void ajouterAppareil(AppareilMedical appareil) {
        appareilDAO.ajouter(appareil);
        listeAppareils.add(appareil);
    }
    
    public boolean supprimerAppareil(long code) {
        boolean supprime = appareilDAO.supprimer(code);
        if (supprime) {
            listeAppareils.removeIf(a -> a.getCode() == code);
        }
        return supprime;
    }
    
    /**
     * Obtient tous les vendables (medicaments + appareils)
     */
    public List<Vendable> getTousVendables() {
        List<Vendable> vendables = new ArrayList<>();
        vendables.addAll(listeMedicaments);
        vendables.addAll(listeAppareils);
        return vendables;
    }
    
    // ============================================
    // GESTION DES ETAGERES
    // ============================================
    
    public void ajouterEtagere(Etagere etagere) {
        etageres.add(etagere);
    }
    
    public List<Etagere> getEtageres() {
        return etageres;
    }
    
    // ============================================
    // GETTERS
    // ============================================
    
    public List<Medicament> getListeMedicaments() {
        return listeMedicaments;
    }
    
    public List<ClientFidele> getListeClientsFideles() {
        return listeClientsFideles;
    }
    
    public List<AppareilMedical> getListeAppareils() {
        return listeAppareils;
    }
    
    public Map<Long, Integer> getMapMedicaments() {
        return mapMedicaments;
    }
    
    public Map<Long, Double> getMapClientsFideles() {
        return mapClientsFideles;
    }
    
    public MedicamentDAO getMedicamentDAO() {
        return medicamentDAO;
    }
    
    public ClientFideleDAO getClientDAO() {
        return clientDAO;
    }
    
    public AppareilMedicalDAO getAppareilDAO() {
        return appareilDAO;
    }
}
