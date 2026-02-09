package dao;

import model.ClientFidele;
import util.DatabaseConnection;
import exception.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gerer les operations CRUD sur les clients fideles dans la base de donnees.
 */
public class ClientFideleDAO {
    
    private Connection connection;
    
    public ClientFideleDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter a la base de donnees", e);
        }
    }
    
    // ============================================
    // CREATE - Ajouter un client
    // ============================================
    
    /**
     * Ajoute un client fidele dans la base de donnees
     */
    public boolean ajouter(ClientFidele client) {
        String sql = """
            INSERT INTO CLIENT_FIDELE (cin, nom, prenom, credit, montant_total_achats)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, client.getCin());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getPrenom());
            pstmt.setDouble(4, client.getCredit());
            pstmt.setDouble(5, client.getMontantTotalAchats());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                throw new DatabaseException("Un client avec ce CIN existe deja", e);
            }
            throw new DatabaseException("Erreur lors de l'ajout du client: " + e.getMessage(), e);
        }
    }
    
    // ============================================
    // READ - Lire les clients
    // ============================================
    
    /**
     * Recupere tous les clients fideles
     */
    public List<ClientFidele> getAll() {
        List<ClientFidele> clients = new ArrayList<>();
        String sql = "SELECT * FROM CLIENT_FIDELE ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recuperation des clients", e);
        }
        return clients;
    }
    
    /**
     * Recupere un client par son CIN
     */
    public ClientFidele getByCin(long cin) {
        String sql = "SELECT * FROM CLIENT_FIDELE WHERE cin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, cin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClient(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recuperation du client", e);
        }
        return null;
    }
    
    /**
     * Recherche les clients par nom (recherche partielle)
     */
    public List<ClientFidele> rechercherParNom(String nom) {
        List<ClientFidele> clients = new ArrayList<>();
        String sql = "SELECT * FROM CLIENT_FIDELE WHERE UPPER(nom) LIKE UPPER(?) ORDER BY nom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par nom", e);
        }
        return clients;
    }
    
    // ============================================
    // UPDATE - Mettre a jour un client
    // ============================================
    
    /**
     * Met a jour un client fidele
     */
    public boolean update(ClientFidele client) {
        String sql = """
            UPDATE CLIENT_FIDELE SET 
                nom = ?, prenom = ?, credit = ?, montant_total_achats = ?
            WHERE cin = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setDouble(3, client.getCredit());
            pstmt.setDouble(4, client.getMontantTotalAchats());
            pstmt.setLong(5, client.getCin());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise a jour du client", e);
        }
    }
    
    /**
     * Met a jour le montant total des achats d'un client
     */
    public boolean updateMontantAchats(long cin, double nouveauMontant) {
        String sql = "UPDATE CLIENT_FIDELE SET montant_total_achats = ? WHERE cin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, nouveauMontant);
            pstmt.setLong(2, cin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise a jour du montant", e);
        }
    }
    
    /**
     * Ajoute un montant aux achats d'un client
     */
    public boolean ajouterMontantAchats(long cin, double montant) {
        String sql = "UPDATE CLIENT_FIDELE SET montant_total_achats = montant_total_achats + ? WHERE cin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setLong(2, cin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'ajout au montant", e);
        }
    }
    
    /**
     * Reinitialise le montant total des achats (apres reduction)
     */
    public boolean reinitialiserMontantAchats(long cin) {
        return updateMontantAchats(cin, 0);
    }
    
    // ============================================
    // DELETE - Supprimer un client
    // ============================================
    
    /**
     * Supprime un client par son CIN
     */
    public boolean supprimer(long cin) {
        String sql = "DELETE FROM CLIENT_FIDELE WHERE cin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, cin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du client", e);
        }
    }
    
    // ============================================
    // Methode utilitaire
    // ============================================
    
    private ClientFidele mapResultSetToClient(ResultSet rs) throws SQLException {
        return new ClientFidele(
                rs.getLong("cin"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getDouble("credit"),
                rs.getDouble("montant_total_achats")
        );
    }
    
    /**
     * Compte le nombre total de clients fideles
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM CLIENT_FIDELE";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des clients", e);
        }
        return 0;
    }
    
    /**
     * Verifie si un client existe
     */
    public boolean existe(long cin) {
        return getByCin(cin) != null;
    }
}
