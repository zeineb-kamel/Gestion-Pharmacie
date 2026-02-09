package model;

import java.time.LocalDate;

/**
 * Classe representant un medicament homeopathique.
 * Herite de Medicament et ajoute l'attribut specifique:
 * - plante utilisee pour la composition
 */
public class MedicamentHomeopathique extends Medicament {
    
    private String planteUtilisee;
    
    // ============================================
    // CONSTRUCTEURS
    // ============================================
    
    public MedicamentHomeopathique() {
        super();
    }
    
    public MedicamentHomeopathique(String nom, String genre) {
        super(nom, genre);
    }
    
    public MedicamentHomeopathique(String nom, String genre, double prix) {
        super(nom, genre, prix);
    }
    
    public MedicamentHomeopathique(String nom, String genre, double prix, String planteUtilisee) {
        super(nom, genre, prix);
        this.planteUtilisee = planteUtilisee;
    }
    
    /**
     * Constructeur complet pour chargement depuis la base de donnees
     */
    public MedicamentHomeopathique(long code, long numSerie, String nom, String genre, double prix,
                                   LocalDate dateExpiration, String planteUtilisee) {
        super(code, numSerie, nom, genre, prix, dateExpiration);
        this.planteUtilisee = planteUtilisee;
    }
    
    // ============================================
    // IMPLEMENTATION METHODES ABSTRAITES
    // ============================================
    
    @Override
    public String getDescription() {
        return String.format("Medicament Homeopathique: %s\n" +
                           "  Genre: %s\n" +
                           "  Prix: %.2f DT\n" +
                           "  Plante utilisee: %s",
                getNom(), getGenre(), getPrix(), planteUtilisee);
    }
    
    @Override
    public String getTypeMedicament() {
        return "HOMEOPATHIQUE";
    }
    
    /**
     * Pour un client fidele, retourne le prix reduit de 10%
     */
    @Override
    public double getTranche(boolean clientFidele) {
        if (clientFidele) {
            return getPrix() * 0.90; // Reduction de 10%
        }
        return getPrix();
    }
    
    // ============================================
    // METHODE toString()
    // ============================================
    
    @Override
    public String toString() {
        return String.format("MedicamentHomeopathique[code=%d, nom=%s, genre=%s, prix=%.2f DT, " +
                           "plante=%s, expiration=%s]",
                getCode(), getNom(), getGenre(), getPrix(), planteUtilisee, getDateExpiration());
    }
    
    // ============================================
    // GETTERS ET SETTERS
    // ============================================
    
    public String getPlanteUtilisee() {
        return planteUtilisee;
    }
    
    public void setPlanteUtilisee(String planteUtilisee) {
        this.planteUtilisee = planteUtilisee;
    }
}
