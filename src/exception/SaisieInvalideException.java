package exception;

/**
 * Exception lancee lors d'erreurs de saisie de donnees.
 * Utilisee pour les validations de formulaires dans l'interface graphique.
 */
public class SaisieInvalideException extends Exception {
    
    private String champConcerne;
    
    public SaisieInvalideException() {
        super("Donnee saisie invalide!");
    }
    
    public SaisieInvalideException(String message) {
        super(message);
    }
    
    public SaisieInvalideException(String champ, String message) {
        super(String.format("Erreur sur le champ '%s': %s", champ, message));
        this.champConcerne = champ;
    }
    
    public String getChampConcerne() {
        return champConcerne;
    }
}
