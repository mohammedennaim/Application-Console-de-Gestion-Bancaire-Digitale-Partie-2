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
    public boolean addFeeRuleParTransaction(UUID accountId, BigDecimal value) {
        String sql = """
            INSERT INTO fee_rules (operation_type, fee_mode, fee_value, currency_code, active, created_at) 
            VALUES (?::operation_type, ?::fee_mode, ?, ?, ?, CURRENT_TIMESTAMP)
            """;
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, "TRANSFER_EXTERNAL");
            ps.setString(2, "FIX");
            ps.setBigDecimal(3, value);
            ps.setString(4, "MAD");
            ps.setBoolean(5, true);
            
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

    public boolean addFeeRuleParCredit(UUID accountId, BigDecimal value) {
        String sql = """
            INSERT INTO fee_rules (operation_type, fee_mode, fee_value, currency_code, active, created_at) 
            VALUES (?::operation_type, ?::fee_mode, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, FeeRule.OperationType.TRANSFER_EXTERNAL.name());
            ps.setString(2, FeeRule.Mode.PERCENT.name());
            ps.setBigDecimal(3, value);
            ps.setString(4, "MAD");
            ps.setBoolean(5, true);

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
        System.err.println("Erreur: update() ne peut pas fonctionner avec UUID sur une table avec ID INTEGER");
        return false;
    }

    @Override
    public boolean delete(UUID accountId) {
        System.err.println("Erreur: delete() ne peut pas fonctionner avec UUID sur une table avec ID INTEGER");
        return false;
    }
}
