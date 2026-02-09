package model;

import exception.EtagerePleineException;
import exception.MedicamentNonTrouveException;

/**
 * Classe representant une etagere qui peut contenir un nombre fixe de medicaments.
 * Basee sur le TP3.
 */
public class Etagere {
    
    private Medicament[] medicaments;
    private int capaciteMax;
    private int nombreMedicaments;
    
    // ============================================
    // CONSTRUCTEUR
    // ============================================
    
    /**
     * Constructeur avec la capacite maximale de l'etagere
     */
    public Etagere(int capaciteMax) {
        this.capaciteMax = capaciteMax;
        this.medicaments = new Medicament[capaciteMax];
        this.nombreMedicaments = 0;
    }
    
    // ============================================
    // METHODES
    // ============================================
    
    /**
     * Retourne le nombre de medicaments contenus dans l'etagere
     * et affiche la capacite maximale
     */
    public int nombreMedicaments() {
        System.out.println("Capacite maximale de l'etagere: " + capaciteMax);
        return nombreMedicaments;
    }
    
    /**
     * Ajoute un medicament a la fin de l'etagere.
     * Lance une exception si l'etagere est pleine.
     */
    public void ajouterMedicament(Medicament m) throws EtagerePleineException {
        if (nombreMedicaments >= capaciteMax) {
            throw new EtagerePleineException("L'etagere est pleine! Capacite maximale: " + capaciteMax);
        }
        medicaments[nombreMedicaments] = m;
        nombreMedicaments++;
    }
    
    /**
     * Recupere un medicament par sa position (1-indexed).
     * La position du premier medicament est 1 (pas 0).
     */
    public Medicament getMedicament(int position) {
        if (position < 1 || position > nombreMedicaments) {
            return null;
        }
        return medicaments[position - 1]; // Convertir en index 0-based
    }
    
    /**
     * Cherche un medicament par son nom et son genre.
     * Retourne la position (1-indexed) ou 0 si non trouve.
     */
    public int chercher(String nom, String genre) {
        for (int i = 0; i < nombreMedicaments; i++) {
            if (medicaments[i].getNom().equalsIgnoreCase(nom) &&
                medicaments[i].getGenre().equalsIgnoreCase(genre)) {
                return i + 1; // Retourner position 1-indexed
            }
        }
        return 0; // Non trouve
    }
    
    /**
     * Enleve un medicament par sa position (1-indexed).
     * Tasse les medicaments vers le debut.
     * Retourne le medicament supprime ou null si non trouve.
     */
    public Medicament enleverMedicament(int position) {
        if (position < 1 || position > nombreMedicaments) {
            return null;
        }
        
        int index = position - 1;
        Medicament medicamentSupprime = medicaments[index];
        
        // Tasser les medicaments vers le debut
        for (int i = index; i < nombreMedicaments - 1; i++) {
            medicaments[i] = medicaments[i + 1];
        }
        medicaments[nombreMedicaments - 1] = null;
        nombreMedicaments--;
        
        return medicamentSupprime;
    }
    
    /**
     * Enleve un medicament par son nom et son genre.
     * Utilise la methode chercher pour trouver la position.
     * Retourne le medicament supprime ou null si non trouve.
     */
    public Medicament enleverMedicament(String nom, String genre) {
        int position = chercher(nom, genre);
        if (position == 0) {
            return null;
        }
        return enleverMedicament(position);
    }
    
    /**
     * Verifie si l'etagere est pleine
     */
    public boolean estPleine() {
        return nombreMedicaments >= capaciteMax;
    }
    
    /**
     * Verifie si l'etagere est vide
     */
    public boolean estVide() {
        return nombreMedicaments == 0;
    }
    
    // ============================================
    // METHODE toString()
    // ============================================
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ETAGERE ===\n");
        sb.append(String.format("Capacite: %d/%d medicaments\n", nombreMedicaments, capaciteMax));
        sb.append("---------------\n");
        
        if (nombreMedicaments == 0) {
            sb.append("(Etagere vide)\n");
        } else {
            for (int i = 0; i < nombreMedicaments; i++) {
                sb.append(String.format("%d. %s\n", i + 1, medicaments[i].getNom()));
            }
        }
        sb.append("===============");
        return sb.toString();
    }
    
    // ============================================
    // GETTERS
    // ============================================
    
    public int getCapaciteMax() {
        return capaciteMax;
    }
    
    public int getNombreMedicaments() {
        return nombreMedicaments;
    }
    
    public Medicament[] getMedicaments() {
        return medicaments;
    }
}
