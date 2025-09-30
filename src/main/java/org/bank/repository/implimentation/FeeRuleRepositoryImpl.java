package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.FeeRule;
import org.bank.repository.FeeRuleRepository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class FeeRuleRepositoryImpl implements FeeRuleRepository {
    DatabaseConnection cnx = DatabaseConnection.getInstance();

    public FeeRuleRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean addFeeRuleParTransaction(UUID accountId, BigDecimal prix) {
        String sql = """
            INSERT INTO fee_rules (operation_type, fee_mode, fee_value, currency_code, active, created_at) 
            VALUES (?::operation_type, ?::fee_mode, ?, ?, ?, CURRENT_TIMESTAMP)
            """;
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, "TRANSFER_EXTERNAL");
            ps.setString(2, "FIX");
            ps.setBigDecimal(3, prix);
            ps.setString(4, "MAD");
            ps.setBoolean(5, true);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Règle de frais ajoutée avec succès: " + prix + " MAD");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la règle de frais: " + e.getMessage());
            return false;
        }
    }

    public boolean addFeeRuleParCredit(UUID accountId, BigDecimal value) {
        String sql = """
            INSERT INTO fee_rules (operation_type, fee_mode, fee_value, currency_code, active, created_at) 
            VALUES (?::operation_type, ?::fee_mode, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, FeeRule.OperationType.TRANSFER_EXTERNAL.name());           // operation_type par défaut
            ps.setString(2, FeeRule.Mode.PERCENT.name());                         // fee_mode par défaut
            ps.setBigDecimal(3, value);                      // fee_value
            ps.setString(4, "MAD");                         // currency_code par défaut
            ps.setBoolean(5, true);                         // active

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Règle de frais ajoutée avec succès: " + value + " MAD");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la règle de frais: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(UUID accountId, BigDecimal value) {
        String sql = "UPDATE fee_rules SET fee_value = ?, created_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setBigDecimal(1, value);
            ps.setObject(2, accountId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Règle de frais mise à jour avec succès");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la règle de frais: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(UUID accountId) {
        String sql = "DELETE FROM fee_rules WHERE id = ?";
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setObject(1, accountId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Règle de frais supprimée avec succès");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la règle de frais: " + e.getMessage());
            return false;
        }
    }
}
