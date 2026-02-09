package model;

import java.time.LocalDate;

/**
 * Classe representant un medicament chimique.
 * Herite de Medicament et ajoute les attributs specifiques:
 * - constituant chimique basique
 * - age minimum d'utilisation
 */
public class MedicamentChimique extends Medicament {
    
    private String constituantChimique;
    private int ageMinimum;
    
    // ============================================
    // CONSTRUCTEURS
    // ============================================
    
    public MedicamentChimique() {
        super();
    }
    
    public MedicamentChimique(String nom, String genre) {
        super(nom, genre);
    }
    
    public MedicamentChimique(String nom, String genre, double prix) {
        super(nom, genre, prix);
    }
    
    public MedicamentChimique(String nom, String genre, double prix, String constituantChimique, int ageMinimum) {
        super(nom, genre, prix);
        this.constituantChimique = constituantChimique;
        this.ageMinimum = ageMinimum;
    }
    
    /**
     * Constructeur complet pour chargement depuis la base de donnees
     */
    public MedicamentChimique(long code, long numSerie, String nom, String genre, double prix, 
                              LocalDate dateExpiration, String constituantChimique, int ageMinimum) {
        super(code, numSerie, nom, genre, prix, dateExpiration);
        this.constituantChimique = constituantChimique;
        this.ageMinimum = ageMinimum;
    }
    
    // ============================================
    // IMPLEMENTATION METHODES ABSTRAITES
    // ============================================
    
    @Override
    public String getDescription() {
        return String.format("Medicament Chimique: %s\n" +
                           "  Genre: %s\n" +
                           "  Prix: %.2f DT\n" +
                           "  Constituant: %s\n" +
                           "  Age minimum: %d ans",
                getNom(), getGenre(), getPrix(), constituantChimique, ageMinimum);
    }
    
    @Override
    public String getTypeMedicament() {
        return "CHIMIQUE";
    }
    
    /**
     * Pour un client fidele, retourne le prix reduit de 20%
     */
    @Override
    public double getTranche(boolean clientFidele) {
        if (clientFidele) {
            return getPrix() * 0.80; // Reduction de 20%
        }
        return getPrix();
    }
    
    // ============================================
    // METHODE toString()
    // ============================================
    
    @Override
    public String toString() {
        return String.format("MedicamentChimique[code=%d, nom=%s, genre=%s, prix=%.2f DT, " +
                           "constituant=%s, ageMin=%d, expiration=%s]",
                getCode(), getNom(), getGenre(), getPrix(), 
                constituantChimique, ageMinimum, getDateExpiration());
    }
    
    // ============================================
    // GETTERS ET SETTERS
    // ============================================
    
    public String getConstituantChimique() {
        return constituantChimique;
    }
    
    public void setConstituantChimique(String constituantChimique) {
        this.constituantChimique = constituantChimique;
    }
    
    public int getAgeMinimum() {
        return ageMinimum;
    }
    
    public void setAgeMinimum(int ageMinimum) {
        this.ageMinimum = ageMinimum;
    }
}
