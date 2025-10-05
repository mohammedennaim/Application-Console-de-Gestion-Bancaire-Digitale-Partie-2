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

    public boolean transfer(
        UUID transactionId,
        Transaction.TransactionType transactionType,
        Transaction.TransactionStatus transactionStatus,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        BigDecimal fee,
        Currency currency,
        UUID initiatedByUserId,
        String externalReference,
        String description
        ) {

        String sql = "INSERT INTO transactions (" +
                "id, transaction_type, transaction_status, " +
                "source_account_id, target_account_id, amount, fee, currency_code, " +
                "initiated_by_user_id, external_reference, description, created_at, executed_at " +
                ") VALUES (?::uuid, ?::transaction_type, ?::transaction_status, ?::uuid, ?::uuid, ?, ?, ?, ?::uuid, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, transactionId.toString());
            ps.setString(2, transactionType.name()); 
            ps.setString(3, transactionStatus.name());

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
            ps.setString(8, currency.getCode());
            ps.setString(9, initiatedByUserId.toString());
            ps.setString(10, externalReference);
            ps.setString(11, description);
            ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));

            if (transactionStatus == Transaction.TransactionStatus.SETTLED) {
                ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                ps.setNull(13, Types.TIMESTAMP);
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors du transfert: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean transferExterne(
        UUID transactionId,
        Transaction.TransactionType transactionType,
        Transaction.TransactionStatus transactionStatus,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        BigDecimal fee,
        Currency currency,
        UUID initiatedByUserId,
        String externalReference,
        String description
        ) {

        String sql = "INSERT INTO transactions (" +
                "id, transaction_type, transaction_status, " +
                "source_account_id, target_account_id, amount, fee, currency_code, " +
                "initiated_by_user_id, external_reference, description, created_at, executed_at " +
                ") VALUES (?::uuid, ?::transaction_type, ?::transaction_status, ?::uuid, ?::uuid, ?, ?, ?, ?::uuid, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.getConnection().prepareStatement(sql)) {
            ps.setString(1, transactionId.toString());
            ps.setString(2, transactionType.name());
            ps.setString(3, transactionStatus.name());

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
            ps.setString(8, currency.getCode());
            ps.setString(9, initiatedByUserId.toString());
            ps.setString(10, externalReference);
            ps.setString(11, description);
            ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));

            if (transactionStatus == Transaction.TransactionStatus.SETTLED) {
                ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                ps.setNull(13, Types.TIMESTAMP);
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors du transfert externe: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}