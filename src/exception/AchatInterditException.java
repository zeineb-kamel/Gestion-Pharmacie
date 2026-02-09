package exception;

/**
 * Exception lancee lorsqu'un client n'est pas autorise a effectuer un achat.
 * Par exemple: abonnement epuise, credit insuffisant, etc.
 */
public class AchatInterditException extends Exception {
    
    public AchatInterditException() {
        super("Achat interdit!");
    }
    
    public AchatInterditException(String message) {
        super(message);
    }
    
    public AchatInterditException(String nomClient, String raison) {
        super(String.format("Achat interdit pour %s: %s", nomClient, raison));
    }
}
