package dao;

import model.AppareilMedical;
import util.DatabaseConnection;
import exception.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gerer les operations CRUD sur les appareils medicaux dans la base de donnees.
 */
public class AppareilMedicalDAO {
    
    private Connection connection;
    
    public AppareilMedicalDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter a la base de donnees", e);
        }
    }
    
    // ============================================
    // CREATE - Ajouter un appareil
    // ============================================
    
    /**
     * Ajoute un appareil medical dans la base de donnees
     */
    public long ajouter(AppareilMedical appareil) {
        String sql = """
            INSERT INTO APPAREIL_MEDICAL (code, nom, prix, quantite_stock)
            VALUES (seq_appareil.NEXTVAL, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"CODE"})) {
            pstmt.setString(1, appareil.getNom());
            pstmt.setDouble(2, appareil.getPrix());
            pstmt.setInt(3, appareil.getQuantiteStock());
            
            pstmt.executeUpdate();
            
            // Recuperer le code genere
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long code = rs.getLong(1);
                    appareil.setCode(code);
                    return code;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'ajout de l'appareil: " + e.getMessage(), e);
        }
        return -1;
    }
    
    // ============================================
    // READ - Lire les appareils
    // ============================================
    
    /**
     * Recupere tous les appareils medicaux
     */
    public List<AppareilMedical> getAll() {
        List<AppareilMedical> appareils = new ArrayList<>();
        String sql = "SELECT * FROM APPAREIL_MEDICAL ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appareils.add(mapResultSetToAppareil(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recuperation des appareils", e);
        }
        return appareils;
    }
    
    /**
     * Recupere un appareil par son code
     */
    public AppareilMedical getByCode(long code) {
        String sql = "SELECT * FROM APPAREIL_MEDICAL WHERE code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, code);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppareil(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recuperation de l'appareil", e);
        }
        return null;
    }
    
    /**
     * Recherche les appareils par nom (recherche partielle)
     */
    public List<AppareilMedical> rechercherParNom(String nom) {
        List<AppareilMedical> appareils = new ArrayList<>();
        String sql = "SELECT * FROM APPAREIL_MEDICAL WHERE UPPER(nom) LIKE UPPER(?) ORDER BY nom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appareils.add(mapResultSetToAppareil(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par nom", e);
        }
        return appareils;
    }
    
    // ============================================
    // UPDATE - Mettre a jour un appareil
    // ============================================
    
    /**
     * Met a jour un appareil medical
     */
    public boolean update(AppareilMedical appareil) {
        String sql = """
            UPDATE APPAREIL_MEDICAL SET 
                nom = ?, prix = ?, quantite_stock = ?
            WHERE code = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, appareil.getNom());
            pstmt.setDouble(2, appareil.getPrix());
            pstmt.setInt(3, appareil.getQuantiteStock());
            pstmt.setLong(4, appareil.getCode());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise a jour de l'appareil", e);
        }
    }
    
    /**
     * Met a jour le stock d'un appareil
     */
    public boolean updateStock(long code, int quantite) {
        String sql = "UPDATE APPAREIL_MEDICAL SET quantite_stock = ? WHERE code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantite);
            pstmt.setLong(2, code);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise a jour du stock", e);
        }
    }
    
    /**
     * Decremente le stock d'un appareil
     */
    public boolean decrementerStock(long code, int quantite) {
        String sql = "UPDATE APPAREIL_MEDICAL SET quantite_stock = quantite_stock - ? WHERE code = ? AND quantite_stock >= ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantite);
            pstmt.setLong(2, code);
            pstmt.setInt(3, quantite);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la decrementation du stock", e);
        }
    }
    
    // ============================================
    // DELETE - Supprimer un appareil
    // ============================================
    
    /**
     * Supprime un appareil par son code
     */
    public boolean supprimer(long code) {
        String sql = "DELETE FROM APPAREIL_MEDICAL WHERE code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, code);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression de l'appareil", e);
        }
    }
    
    // ============================================
    // Methode utilitaire
    // ============================================
    
    private AppareilMedical mapResultSetToAppareil(ResultSet rs) throws SQLException {
        return new AppareilMedical(
                rs.getLong("code"),
                rs.getString("nom"),
                rs.getDouble("prix"),
                rs.getInt("quantite_stock")
        );
    }
    
    /**
     * Compte le nombre total d'appareils medicaux
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM APPAREIL_MEDICAL";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des appareils", e);
        }
        return 0;
    }
}
