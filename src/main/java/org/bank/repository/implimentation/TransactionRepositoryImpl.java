package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Transaction;
import org.bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import org.bank.domain.Currency;
import java.util.UUID;

public class TransactionRepositoryImpl implements TransactionRepository {
    DatabaseConnection cnx = DatabaseConnection.getInstance();

    public TransactionRepositoryImpl() throws SQLException {

    }

    public boolean transfer(UUID transactionId,
                           Transaction.TransactionType transactionType,
                           Transaction.TransactionStatus transactionStatus,
                           UUID sourceAccountId,
                           UUID targetAccountId,
                           BigDecimal amount,
                           BigDecimal fee,
                           Currency currency,
                           UUID initiatedByUserId,
                           String externalReference,
                           String description) {

        String sql = "INSERT INTO transactions (" +
                "id, transaction_type, transaction_status, " +
                "source_account_id, target_account_id, amount, fee, currency_code, " +
                "initiated_by_user_id, external_reference, description, created_at, executed_at " +
                ") VALUES (?::uuid, ?::transaction_type, ?::transaction_status, " +
                "?::uuid, ?::uuid, ?, ?, ?, ?::uuid, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, transactionId.toString());
            ps.setString(2, transactionType.name()); // DEPOSIT, WITHDRAW, etc.
            ps.setString(3, transactionStatus.name()); // PENDING, SETTLED, etc.

            if (sourceAccountId != null) {
                ps.setString(4, sourceAccountId.toString());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (targetAccountId != null) {
                ps.setString(5, targetAccountId.toString());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setBigDecimal(6, amount);
            ps.setBigDecimal(7, fee != null ? fee : BigDecimal.ZERO);
            ps.setObject(8, currency); // ex: "MAD"
            ps.setString(9, initiatedByUserId.toString());
            ps.setString(10, externalReference);
            ps.setString(11, description);
            ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now())); // created_at

            if (transactionStatus == Transaction.TransactionStatus.SETTLED) {
                ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                ps.setNull(13, Types.TIMESTAMP);
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean transferExterne(UUID transactionId,
                                   Transaction.TransactionType transactionType,
                                   Transaction.TransactionStatus transactionStatus,
                                   UUID sourceAccountId,
                                   UUID targetAccountId,
                                   BigDecimal amount,
                                   BigDecimal fee,
                                   Currency currency,
                                   UUID initiatedByUserId,
                                   String externalReference,
                                   String description) {

        String sql = "INSERT INTO transactions (" +
                "id, transaction_type, transaction_status, " +
                "source_account_id, target_account_id, amount, fee, currency_code, " +
                "initiated_by_user_id, external_reference, description, created_at, executed_at " +
                ") VALUES (?::uuid, ?::transaction_type, ?::transaction_status, " +
                "?::uuid, ?::uuid, ?, ?, ?, ?::uuid, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, transactionId.toString());
            ps.setString(2, transactionType.name()); // DEPOSIT, WITHDRAW, etc.
            ps.setString(3, transactionStatus.name()); // PENDING, SETTLED, etc.

            if (sourceAccountId != null) {
                ps.setString(4, sourceAccountId.toString());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (targetAccountId != null) {
                ps.setString(5, targetAccountId.toString());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setBigDecimal(6, amount);
            ps.setBigDecimal(7, fee != null ? fee : BigDecimal.ZERO);
            ps.setObject(8, currency); // ex: "MAD"
            ps.setString(9, initiatedByUserId.toString());
            ps.setString(10, externalReference);
            ps.setString(11, description);
            ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now())); // created_at

            if (transactionStatus == Transaction.TransactionStatus.SETTLED) {
                ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                ps.setNull(13, Types.TIMESTAMP);
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public boolean delete(UUID transactionId) {
        String sql = "UPDATE transactions SET deleted = true, deleted_at = ? WHERE id = ?::uuid AND deleted = false";
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, transactionId.toString());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression logique de la transaction: " + e.getMessage());
            return false;
        }
    }

    public boolean restore(UUID transactionId) {
        String sql = "UPDATE transactions SET deleted = false, deleted_at = null WHERE id = ?::uuid AND deleted = true";
        
        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, transactionId.toString());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la restauration de la transaction: " + e.getMessage());
            return false;
        }
    }

}
