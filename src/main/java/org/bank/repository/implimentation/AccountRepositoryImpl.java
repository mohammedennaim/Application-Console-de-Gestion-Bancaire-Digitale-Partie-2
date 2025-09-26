package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Account;
import org.bank.repository.AccountRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.UUID;

public class AccountRepositoryImpl implements AccountRepository {
    private final DatabaseConnection connection;

    public AccountRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }


    public Account findById(UUID id) {
        Connection cnx = this.connection.getConnection();
        String sql = "SELECT * FROM accounts WHERE id::text = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setObject(1, id.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setId((UUID) rs.getObject("id"));
                account.setOwnerId((UUID) rs.getObject("client_id"));
                account.setType(Account.AccountType.valueOf(rs.getString("account_type")));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCurrency(Currency.getInstance(rs.getString("currency_code")));
                account.setOverdraftAllowed(rs.getBoolean("overdraft_allowed"));
                account.setOverdraftLimit(rs.getBigDecimal("overdraft_limit"));
                account.setClosed(rs.getBoolean("closed"));
                account.setOpenedAt(rs.getTimestamp("opened_at") != null ?
                    rs.getTimestamp("opened_at").toLocalDateTime() : null);
                account.setClosedAt(rs.getTimestamp("closed_at") != null ?
                    rs.getTimestamp("closed_at").toLocalDateTime() : null);
                account.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);
                return account;
            }

        } catch (SQLException e) {
            System.err.println("erruer lors de recherche de ID " +  id + e.getMessage());
        }
        return null;
    }
    @Override
    public boolean save(Account account) {
        Connection cnx = this.connection.getConnection();
        String sql = """
        INSERT INTO accounts (
            id, client_id, account_type, balance, currency_code,
            overdraft_allowed, overdraft_limit, closed,
            opened_at, closed_at
        ) VALUES (?, ?, ?::account_type, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            String accountId = account.getId().toString();
            if (accountId.startsWith("BK-") || accountId.startsWith("CR-") || accountId.startsWith("TX-")) {
                ps.setString(1, accountId);
            } else {
                ps.setObject(1, account.getId());
            }

            ps.setObject(2, account.getOwnerId());
            ps.setString(3, account.getType().name());
            ps.setBigDecimal(4, account.getBalance());
            ps.setString(5, account.getCurrency().getCurrencyCode());
            ps.setBoolean(6, account.isOverdraftAllowed());
            ps.setBigDecimal(7, account.getOverdraftLimit());
            ps.setBoolean(8, account.isClosed());
            ps.setObject(9, account.getOpenedAt());
            ps.setObject(10, account.getClosedAt());

            ps.executeUpdate();
            System.out.println("Compte inséré avec succès : " + account.getId());
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
