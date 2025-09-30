package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Credit;
import org.bank.repository.CreditRepository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Override
    public boolean credit(BigDecimal amount, UUID clientID, UUID accountID, BigDecimal fee, Credit.InterestMode type) {
        String sql = """
            INSERT INTO credits (
                id, linked_account_id, requested_amount, interest_rate, 
                term_months, status, interest_mode, requested_by_client_id, created_at
            ) VALUES (
                ?::uuid, ?::uuid, ?, ?, ?, ?::credit_status, ?::interest_mode, ?::uuid, ?
            )
            """;
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, accountID.toString());
            ps.setBigDecimal(3, amount);
            ps.setBigDecimal(4, fee);
            ps.setInt(5, 12);
            ps.setString(6, Credit.CreditStatus.REQUESTED.toString());
            ps.setString(7, type.toString());
            ps.setString(8, clientID.toString());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Demande de crédit de " + amount + " créée avec succès pour le client " + clientID);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du crédit: " + e.getMessage());
            return false;
        }
    }
}
