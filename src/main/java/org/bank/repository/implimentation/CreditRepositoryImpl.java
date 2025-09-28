package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.repository.CreditRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreditRepositoryImpl implements CreditRepository {
    DatabaseConnection cnx = DatabaseConnection.getInstance();

    public CreditRepositoryImpl() throws SQLException {
    }

    public boolean delete(UUID creditId) {
        String sql = "UPDATE credits SET deleted = true, deleted_at = ? WHERE id = ?::uuid AND deleted = false";
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, creditId.toString());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression logique du crédit: " + e.getMessage());
            return false;
        }
    }

    public boolean restore(UUID creditId) {
        String sql = "UPDATE credits SET deleted = false, deleted_at = null WHERE id = ?::uuid AND deleted = true";
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, creditId.toString());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la restauration du crédit: " + e.getMessage());
            return false;
        }
    }
}
