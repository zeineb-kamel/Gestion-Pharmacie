package exception;

/**
 * Exception lancee lorsqu'un medicament n'est pas trouve.
 */
public class MedicamentNonTrouveException extends Exception {
    
    public MedicamentNonTrouveException() {
        super("Medicament non trouve!");
    }
    
    public MedicamentNonTrouveException(String message) {
        super(message);
    }
    
    public MedicamentNonTrouveException(String nom, String genre) {
        super(String.format("Medicament '%s' de genre '%s' non trouve!", nom, genre));
    }
}
