package model;

/**
 * Classe representant un client fidele de la pharmacie.
 * Un client fidele est identifie par son CIN, son nom et son prenom.
 * Il dispose d'un credit et d'un montant total d'achats pour les reductions.
 */
public class ClientFidele {
    
    private long cin;
    private String nom;
    private String prenom;
    private double credit;
    private double montantTotalAchats;
    
    // ============================================
    // CONSTRUCTEURS
    // ============================================
    
    public ClientFidele() {
    }
    
    public ClientFidele(long cin, String nom, String prenom) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.credit = 0;
        this.montantTotalAchats = 0;
    }
    
    /**
     * Constructeur complet pour chargement depuis la base de donnees
     */
    public ClientFidele(long cin, String nom, String prenom, double credit, double montantTotalAchats) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.credit = credit;
        this.montantTotalAchats = montantTotalAchats;
    }
    
    // ============================================
    // METHODES METIER
    // ============================================
    
    /**
     * Ajoute un montant aux achats du client.
     * Si le montant total depasse 100 DT, le client aura une reduction de 15%
     * sur le prochain achat et son montant sera reinitialise.
     */
    public void ajouterAchat(double montant) {
        this.montantTotalAchats += montant;
    }
    
    /**
     * Verifie si le client a droit a une reduction de 15%
     * (montant total achats >= 100 DT)
     */
    public boolean aReduction() {
        return this.montantTotalAchats >= 100;
    }
    
    /**
     * Applique la reduction et reinitialise le montant total
     */
    public void appliquerReductionEtReinitialiser() {
        this.montantTotalAchats = 0;
    }
    
    /**
     * Ajoute du credit au compte du client
     */
    public void ajouterCredit(double montant) {
        this.credit += montant;
    }
    
    /**
     * Deduit du credit du compte du client
     */
    public void deduireCredit(double montant) {
        this.credit -= montant;
    }
    
    // ============================================
    // METHODE toString()
    // ============================================
    
    @Override
    public String toString() {
        return String.format("ClientFidele[cin=%d, nom=%s, prenom=%s, credit=%.2f DT, totalAchats=%.2f DT]",
                cin, nom, prenom, credit, montantTotalAchats);
    }
    
    /**
     * Retourne le nom complet du client
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    // ============================================
    // GETTERS ET SETTERS
    // ============================================
    
    public long getCin() {
        return cin;
    }
    
    public void setCin(long cin) {
        this.cin = cin;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public double getCredit() {
        return credit;
    }
    
    public void setCredit(double credit) {
        this.credit = credit;
    }
    
    public double getMontantTotalAchats() {
        return montantTotalAchats;
    }
    
    public void setMontantTotalAchats(double montantTotalAchats) {
        this.montantTotalAchats = montantTotalAchats;
    }
}
