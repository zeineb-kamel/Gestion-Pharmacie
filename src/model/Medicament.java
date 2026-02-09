package model;

import java.time.LocalDate;

/**
 * Classe abstraite representant un medicament.
 * Un medicament peut etre soit chimique soit homeopathique.
 * Implemente l'interface Vendable pour permettre la vente.
 */
public abstract class Medicament implements Vendable, Comparable<Medicament> {
    
    // Compteur statique pour generer des codes uniques
    private static long compteurCode = 0;
    
    // Attributs
    private long code;
    private long numSerie;
    private String nom;
    private String genre;
    private double prix;
    private LocalDate dateExpiration;
    private int quantiteStock;
    
    // ============================================
    // CONSTRUCTEURS
    // ============================================
    
    /**
     * Constructeur sans parametres
     */
    public Medicament() {
        this.code = ++compteurCode;
    }
    
    /**
     * Constructeur avec nom et genre
     */
    public Medicament(String nom, String genre) {
        this.code = ++compteurCode;
        this.nom = nom;
        this.genre = genre;
    }
    
    /**
     * Constructeur avec nom, genre et prix
     */
    public Medicament(String nom, String genre, double prix) {
        this.code = ++compteurCode;
        this.nom = nom;
        this.genre = genre;
        this.prix = prix;
    }
    
    /**
     * Constructeur complet
     */
    public Medicament(long code, long numSerie, String nom, String genre, double prix, LocalDate dateExpiration) {
        this.code = code;
        this.numSerie = numSerie;
        this.nom = nom;
        this.genre = genre;
        this.prix = prix;
        this.dateExpiration = dateExpiration;
        // Mettre a jour le compteur si le code est superieur
        if (code > compteurCode) {
            compteurCode = code;
        }
    }
    
    // ============================================
    // METHODES DE COMPARAISON (TP2)
    // ============================================
    
    /**
     * Compare ce medicament avec un autre selon l'ordre alphabetique des noms.
     * Methode d'instance (non statique car utilise this)
     */
    public int compareNom(Medicament m) {
        return this.nom.compareToIgnoreCase(m.getNom());
    }
    
    /**
     * Compare deux medicaments selon l'ordre alphabetique des noms.
     * Methode statique car ne depend pas de l'instance courante
     */
    public static int compareNom(Medicament m1, Medicament m2) {
        return m1.getNom().compareToIgnoreCase(m2.getNom());
    }
    
    /**
     * Compare ce medicament avec un autre selon leurs prix.
     * Methode d'instance
     */
    public int comparePrix(Medicament m) {
        return Double.compare(this.prix, m.getPrix());
    }
    
    /**
     * Compare deux medicaments selon leurs prix.
     * Methode statique
     */
    public static int comparePrix(Medicament m1, Medicament m2) {
        return Double.compare(m1.getPrix(), m2.getPrix());
    }
    
    /**
     * Implementation de Comparable pour permettre le tri avec Arrays.sort()
     */
    @Override
    public int compareTo(Medicament m) {
        return this.compareNom(m);
    }
    
    // ============================================
    // METHODE ABSTRAITE (pour polymorphisme)
    // ============================================
    
    /**
     * Retourne une description specifique selon le type de medicament
     */
    public abstract String getDescription();
    
    /**
     * Retourne le type de medicament (CHIMIQUE ou HOMEOPATHIQUE)
     */
    public abstract String getTypeMedicament();
    
    // ============================================
    // IMPLEMENTATION INTERFACE VENDABLE
    // ============================================
    
    @Override
    public String getNomVendable() {
        return this.nom;
    }
    
    @Override
    public double getPrixVendable() {
        return this.prix;
    }
    
    // getTranche() sera implemente dans les sous-classes
    
    // ============================================
    // METHODE toString()
    // ============================================
    
    @Override
    public String toString() {
        return String.format("Medicament[code=%d, nom=%s, genre=%s, prix=%.2f DT, expiration=%s]",
                code, nom, genre, prix, dateExpiration);
    }
    
    // ============================================
    // GETTERS ET SETTERS
    // ============================================
    
    public long getCode() {
        return code;
    }
    
    public void setCode(long code) {
        this.code = code;
    }
    
    public long getNumSerie() {
        return numSerie;
    }
    
    public void setNumSerie(long numSerie) {
        this.numSerie = numSerie;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public int getQuantiteStock() {
        return quantiteStock;
    }
    
    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }
    
    /**
     * Verifie si le medicament expire dans un certain nombre de mois
     */
    public boolean expireDans(int mois) {
        if (dateExpiration == null) return false;
        LocalDate dateLimite = LocalDate.now().plusMonths(mois);
        return dateExpiration.isBefore(dateLimite) || dateExpiration.isEqual(dateLimite);
    }
    
    /**
     * Applique une remise sur le prix
     */
    public void appliquerRemise(double pourcentage) {
        this.prix = this.prix * (1 - pourcentage / 100);
    }
}
