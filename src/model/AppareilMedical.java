package model;

/**
 * Classe representant un appareil medical.
 * Implemente l'interface Vendable.
 * Caracteristiques: nom et prix.
 * Facilite de paiement: 3 tranches pour les clients fideles.
 */
public class AppareilMedical implements Vendable {
    
    private static long compteurCode = 0;
    
    private long code;
    private String nom;
    private double prix;
    private int quantiteStock;
    
    // ============================================
    // CONSTRUCTEURS
    // ============================================
    
    public AppareilMedical() {
        this.code = ++compteurCode;
    }
    
    public AppareilMedical(String nom, double prix) {
        this.code = ++compteurCode;
        this.nom = nom;
        this.prix = prix;
    }
    
    /**
     * Constructeur complet pour chargement depuis la base de donnees
     */
    public AppareilMedical(long code, String nom, double prix, int quantiteStock) {
        this.code = code;
        this.nom = nom;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        // Mettre a jour le compteur si le code est superieur
        if (code > compteurCode) {
            compteurCode = code;
        }
    }
    
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
    
    /**
     * Pour un appareil medical, on le vend par facilite de 3 tranches.
     * La methode retourne le montant d'une tranche.
     */
    @Override
    public double getTranche(boolean clientFidele) {
        if (clientFidele) {
            return this.prix / 3.0; // Facilite en 3 tranches
        }
        return this.prix; // Prix plein pour non-fidele
    }
    
    // ============================================
    // METHODE toString()
    // ============================================
    
    @Override
    public String toString() {
        return String.format("AppareilMedical[code=%d, nom=%s, prix=%.2f DT, stock=%d]",
                code, nom, prix, quantiteStock);
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
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public int getQuantiteStock() {
        return quantiteStock;
    }
    
    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }
}
