package exception;

/**
 * Exception lancee lorsqu'une etagere est pleine et qu'on essaie d'ajouter un medicament.
 */
public class EtagerePleineException extends Exception {
    
    public EtagerePleineException() {
        super("L'etagere est pleine!");
    }
    
    public EtagerePleineException(String message) {
        super(message);
    }
}
