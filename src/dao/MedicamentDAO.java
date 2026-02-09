package dao;

import model.Medicament;
import model.MedicamentChimique;
import model.MedicamentHomeopathique;
import util.DatabaseConnection;
import exception.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gerer les operations CRUD sur les medicaments dans la base de donnees.
 */
public class MedicamentDAO {
    
    private Connection connection;
    
    public MedicamentDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter a la base de donnees", e);
        }
    }
    
    // ============================================
    // CREATE - Ajouter un medicament
    // ============================================
    
    /**
     * Ajoute un medicament dans la base de donnees.
     * Le code est genere automatiquement par la sequence Oracle.
     */
    public long ajouter(Medicament medicament) {
        String sql;
        
        if (medicament instanceof MedicamentChimique) {
            sql = """
                INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, 
                    type_medicament, constituant_chimique, age_minimum, quantite_stock)
                VALUES (seq_medicament.NEXTVAL, ?, ?, ?, ?, ?, 'CHIMIQUE', ?, ?, ?)
                """;
        } else {
            sql = """
                INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, 
                    type_medicament, plante_utilisee, quantite_stock)
                VALUES (seq_medicament.NEXTVAL, ?, ?, ?, ?, ?, 'HOMEOPATHIQUE', ?, ?)
                """;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"CODE"})) {
            int index = 1;
            pstmt.setLong(index++, medicament.getNumSerie());
            pstmt.setString(index++, medicament.getNom());
            pstmt.setString(index++, medicament.getGenre());
            pstmt.setDouble(index++, medicament.getPrix());
            pstmt.setDate(index++, medicament.getDateExpiration() != null ? 
                    Date.valueOf(medicament.getDateExpiration()) : null);
            
            if (medicament instanceof MedicamentChimique mc) {
                pstmt.setString(index++, mc.getConstituantChimique());
                pstmt.setInt(index++, mc.getAgeMinimum());
            } else if (medicament instanceof MedicamentHomeopathique mh) {
                pstmt.setString(index++, mh.getPlanteUtilisee());
            }
            pstmt.setInt(index, medicament.getQuantiteStock());
            
            pstmt.executeUpdate();
            
            // Recuperer le code genere
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long code = rs.getLong(1);
                    medicament.setCode(code);
                    return code;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'ajout du medicament: " + e.getMessage(), e);
        }
        return -1;
    }
    
    // ============================================
    // READ - Lire les medicaments
    // ============================================
    
    /**
     * Recupere tous les medicaments de la base de donnees
     */
    public List<Medicament> getAll() {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicaments.add(mapResultSetToMedicament(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recuperation des medicaments", e);
        }
        return medicaments;
    }
    
    /**
     * Recupere un medicament par son code
     */
    public Medicament getByCode(long code) {
        String sql = "SELECT * FROM MEDICAMENT WHERE code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, code);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicament(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recuperation du medicament", e);
        }
        return null;
    }
    
    /**
     * Recherche les medicaments par nom (recherche partielle)
     */
    public List<Medicament> rechercherParNom(String nom) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT WHERE UPPER(nom) LIKE UPPER(?) ORDER BY nom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(mapResultSetToMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par nom", e);
        }
        return medicaments;
    }
    
    /**
     * Recherche les medicaments par categorie (type)
     */
    public List<Medicament> rechercherParCategorie(String categorie) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT WHERE type_medicament = ? ORDER BY nom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categorie.toUpperCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(mapResultSetToMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par categorie", e);
        }
        return medicaments;
    }
    
    /**
     * Recherche les medicaments dont le nom commence par certaines lettres
     */
    public List<Medicament> rechercherParPremieresLettres(String lettres) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT WHERE UPPER(nom) LIKE UPPER(?) ORDER BY nom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lettres + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(mapResultSetToMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par premieres lettres", e);
        }
        return medicaments;
    }
    
    /**
     * Recupere les medicaments qui expirent dans un certain nombre de mois
     */
    public List<Medicament> getMedicamentsExpirantDans(int mois) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT WHERE date_expiration <= ADD_MONTHS(SYSDATE, ?) ORDER BY date_expiration";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, mois);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(mapResultSetToMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche des medicaments expirants", e);
        }
        return medicaments;
    }
    
    // ============================================
    // UPDATE - Mettre a jour un medicament
    // ============================================
    
    /**
     * Met a jour un medicament dans la base de donnees
     */
    public boolean update(Medicament medicament) {
        String sql;
        
        if (medicament instanceof MedicamentChimique) {
            sql = """
                UPDATE MEDICAMENT SET 
                    num_serie = ?, nom = ?, genre = ?, prix = ?, date_expiration = ?,
                    constituant_chimique = ?, age_minimum = ?, quantite_stock = ?
                WHERE code = ?
                """;
        } else {
            sql = """
                UPDATE MEDICAMENT SET 
                    num_serie = ?, nom = ?, genre = ?, prix = ?, date_expiration = ?,
                    plante_utilisee = ?, quantite_stock = ?
                WHERE code = ?
                """;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int index = 1;
            pstmt.setLong(index++, medicament.getNumSerie());
            pstmt.setString(index++, medicament.getNom());
            pstmt.setString(index++, medicament.getGenre());
            pstmt.setDouble(index++, medicament.getPrix());
            pstmt.setDate(index++, medicament.getDateExpiration() != null ? 
                    Date.valueOf(medicament.getDateExpiration()) : null);
            
            if (medicament instanceof MedicamentChimique mc) {
                pstmt.setString(index++, mc.getConstituantChimique());
                pstmt.setInt(index++, mc.getAgeMinimum());
            } else if (medicament instanceof MedicamentHomeopathique mh) {
                pstmt.setString(index++, mh.getPlanteUtilisee());
            }
            pstmt.setInt(index++, medicament.getQuantiteStock());
            pstmt.setLong(index, medicament.getCode());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise a jour du medicament", e);
        }
    }
    
    /**
     * Applique une remise sur les medicaments qui expirent bientot
     */
    public int appliquerRemiseMedicamentsExpirants(int mois, double pourcentageRemise) {
        String sql = "UPDATE MEDICAMENT SET prix = prix * ? WHERE date_expiration <= ADD_MONTHS(SYSDATE, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, 1 - pourcentageRemise / 100);
            pstmt.setInt(2, mois);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'application de la remise", e);
        }
    }
    
    /**
     * Met a jour le stock d'un medicament
     */
    public boolean updateStock(long code, int quantite) {
        String sql = "UPDATE MEDICAMENT SET quantite_stock = ? WHERE code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantite);
            pstmt.setLong(2, code);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise a jour du stock", e);
        }
    }
    
    /**
     * Decremente le stock d'un medicament
     */
    public boolean decrementerStock(long code, int quantite) {
        String sql = "UPDATE MEDICAMENT SET quantite_stock = quantite_stock - ? WHERE code = ? AND quantite_stock >= ?";
        
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
    // DELETE - Supprimer un medicament
    // ============================================
    
    /**
     * Supprime un medicament par son code
     */
    public boolean supprimer(long code) {
        String sql = "DELETE FROM MEDICAMENT WHERE code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, code);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du medicament", e);
        }
    }
    
    /**
     * Supprime un medicament par son nom
     */
    public boolean supprimerParNom(String nom) {
        String sql = "DELETE FROM MEDICAMENT WHERE UPPER(nom) = UPPER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du medicament", e);
        }
    }
    
    // ============================================
    // Methode utilitaire pour mapper ResultSet vers Medicament
    // ============================================
    
    private Medicament mapResultSetToMedicament(ResultSet rs) throws SQLException {
        String type = rs.getString("type_medicament");
        
        long code = rs.getLong("code");
        long numSerie = rs.getLong("num_serie");
        String nom = rs.getString("nom");
        String genre = rs.getString("genre");
        double prix = rs.getDouble("prix");
        Date dateExp = rs.getDate("date_expiration");
        LocalDate dateExpiration = dateExp != null ? dateExp.toLocalDate() : null;
        int quantiteStock = rs.getInt("quantite_stock");
        
        Medicament medicament;
        
        if ("CHIMIQUE".equals(type)) {
            String constituant = rs.getString("constituant_chimique");
            int ageMin = rs.getInt("age_minimum");
            medicament = new MedicamentChimique(code, numSerie, nom, genre, prix, dateExpiration, constituant, ageMin);
        } else {
            String plante = rs.getString("plante_utilisee");
            medicament = new MedicamentHomeopathique(code, numSerie, nom, genre, prix, dateExpiration, plante);
        }
        
        medicament.setQuantiteStock(quantiteStock);
        return medicament;
    }
    
    /**
     * Compte le nombre total de medicaments
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM MEDICAMENT";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des medicaments", e);
        }
        return 0;
    }
}
