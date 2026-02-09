package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gerer la connexion a la base de donnees Oracle.
 * Utilise le pattern Singleton pour garantir une seule connexion.
 */
public class DatabaseConnection {
    
    // Parametres de connexion Oracle
    private static final String HOST = "localhost";
    private static final String PORT = "1521";
    private static final String SID = "xe";
    private static final String USER = "system";
    private static final String PASSWORD = "system";
    
    // URL de connexion JDBC Oracle
    private static final String URL = String.format(
            "jdbc:oracle:thin:@%s:%s:%s", HOST, PORT, SID);
    
    // Instance unique (Singleton)
    private static DatabaseConnection instance;
    private Connection connection;
    
    /**
     * Constructeur prive (Singleton)
     */
    private DatabaseConnection() {
        try {
            // Charger le driver Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver Oracle charge avec succes.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERREUR: Driver Oracle non trouve!");
            System.err.println("Assurez-vous que ojdbc11.jar est dans le classpath.");
            throw new RuntimeException("Driver Oracle non trouve", e);
        }
    }
    
    /**
     * Obtenir l'instance unique de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Obtenir une connexion a la base de donnees.
     * Cree une nouvelle connexion si necessaire.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion a Oracle etablie avec succes.");
        }
        return connection;
    }
    
    /**
     * Fermer la connexion a la base de donnees
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion a Oracle fermee.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    
    /**
     * Tester la connexion a la base de donnees
     */
    public static boolean testConnection() {
        try {
            Connection conn = getInstance().getConnection();
            boolean valid = conn != null && !conn.isClosed();
            System.out.println("Test de connexion: " + (valid ? "REUSSI" : "ECHOUE"));
            return valid;
        } catch (SQLException e) {
            System.err.println("Test de connexion ECHOUE: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Methode main pour tester la connexion
     */
    public static void main(String[] args) {
        System.out.println("=== Test de connexion a Oracle ===");
        System.out.println("URL: " + URL);
        System.out.println("User: " + USER);
        System.out.println();
        
        if (testConnection()) {
            System.out.println("\nConnexion reussie! La base de donnees est accessible.");
        } else {
            System.out.println("\nEchec de la connexion. Verifiez:");
            System.out.println("1. Oracle XE est demarre");
            System.out.println("2. Les parametres de connexion sont corrects");
            System.out.println("3. ojdbc11.jar est dans le classpath");
        }
        
        // Fermer la connexion
        getInstance().closeConnection();
    }
}
