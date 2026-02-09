package exception;

/**
 * Exception lancee lorsque le stock d'un produit est insuffisant.
 */
public class StockInsuffisantException extends Exception {
    
    private int stockActuel;
    private int quantiteDemandee;
    
    public StockInsuffisantException() {
        super("Stock insuffisant!");
    }
    
    public StockInsuffisantException(String message) {
        super(message);
    }
    
    public StockInsuffisantException(String nomProduit, int stockActuel, int quantiteDemandee) {
        super(String.format("Stock insuffisant pour '%s': disponible=%d, demande=%d",
                nomProduit, stockActuel, quantiteDemandee));
        this.stockActuel = stockActuel;
        this.quantiteDemandee = quantiteDemandee;
    }
    
    public int getStockActuel() {
        return stockActuel;
    }
    
    public int getQuantiteDemandee() {
        return quantiteDemandee;
    }
}
