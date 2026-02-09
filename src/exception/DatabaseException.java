package exception;

/**
 * Exception lancee lors d'erreurs de connexion a la base de donnees.
 */
public class DatabaseException extends RuntimeException {
    
    public DatabaseException() {
        super("Erreur de base de donnees!");
    }
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
