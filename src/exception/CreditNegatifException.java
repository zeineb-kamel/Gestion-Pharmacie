package exception;

/**
 * Exception lancee lorsque le credit d'un client est negatif.
 * Basee sur le TP6.
 */
public class CreditNegatifException extends Exception {
    
    private double credit;
    
    public CreditNegatifException() {
        super("Le credit ne peut pas etre negatif!");
    }
    
    public CreditNegatifException(String message) {
        super(message);
    }
    
    public CreditNegatifException(double credit) {
        super(String.format("Credit negatif detecte: %.2f DT", credit));
        this.credit = credit;
    }
    
    public double getCredit() {
        return credit;
    }
}
