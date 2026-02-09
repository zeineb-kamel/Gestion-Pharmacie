package model;

/**
 * Interface Vendable representant tout ce qui peut se vendre dans la pharmacie.
 * Implementee par Medicament (et ses sous-classes) et AppareilMedical.
 */
public interface Vendable {
    
    /**
     * Retourne le nom du produit vendable
     */
    String getNomVendable();
    
    /**
     * Retourne le prix du produit vendable
     */
    double getPrixVendable();
    
    /**
     * Retourne le prix selon le type de client et le type de produit:
     * - Medicament homeopathique: prix reduit de 10% pour client fidele
     * - Medicament chimique: prix reduit de 20% pour client fidele
     * - Appareil medical: prix divise en 3 tranches (facilite de paiement)
     * 
     * @param clientFidele true si le client est fidele
     * @return le prix ou la tranche a payer
     */
    double getTranche(boolean clientFidele);
}
