package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Account;
import org.bank.domain.Client;
import org.bank.domain.Credit;
import org.bank.domain.Currency;
import org.bank.repository.AccountRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRepositoryImpl implements AccountRepository {
    private final DatabaseConnection connection;
    CreditRepositoryImpl creditRepository = new CreditRepositoryImpl();

    public AccountRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }

    public Account getAccountById(UUID id) {
        if (id == null) {
            System.err.println("Erreur: ID ne peut pas être null");
            return null;
        }

        String sql = "SELECT * FROM accounts WHERE id::text = ? AND (deleted = false OR deleted IS NULL)";
        try (Connection cnx = this.connection.getConnection();
                PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setId((UUID) rs.getObject("id"));
                account.setOwnerId((UUID) rs.getObject("client_id"));
                account.setType(Account.AccountType.valueOf(rs.getString("account_type")));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCurrency(Currency.fromCode(rs.getString("currency_code")));
                account.setClosed(rs.getBoolean("closed"));
                account.setOpenedAt(
                        rs.getTimestamp("opened_at") != null ? rs.getTimestamp("opened_at").toLocalDateTime() : null);
                account.setClosedAt(
                        rs.getTimestamp("closed_at") != null ? rs.getTimestamp("closed_at").toLocalDateTime() : null);
                account.setUpdatedAt(
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                return account;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de recherche de ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    public boolean findById(UUID id) {
        return getAccountById(id) != null;
    }

    public Account getAccountByClientId(UUID clientId) {
        String sql = "SELECT * FROM accounts WHERE client_id::text = ? and account_type = ? AND (deleted = false OR deleted IS NULL)";
        try (Connection cnx = this.connection.getConnection();
                PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, clientId.toString());
            ps.setObject(2, Account.AccountType.CREDIT);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("client_id")),
                    Account.AccountType.valueOf(rs.getString("account_type")),
                    rs.getBigDecimal("balance"),
                    Currency.valueOf(rs.getString("currency_code")),
                    rs.getTimestamp("opened_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de recherche de client ID " + clientId + ": " + e.getMessage());
        }
        return null;
    }

    public Account getCreditAccountByClientId(UUID clientId) {
        String sql = "SELECT * FROM accounts WHERE client_id::text = ? AND account_type = 'CREDIT' AND closed = false";
        try (Connection cnx = this.connection.getConnection();
                PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, clientId.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setId((UUID) rs.getObject("id"));
                account.setOwnerId((UUID) rs.getObject("client_id"));
                account.setType(Account.AccountType.valueOf(rs.getString("account_type")));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCurrency(Currency.fromCode(rs.getString("currency_code")));
                return account;
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de recherche de compte crédit pour client ID " + clientId + ": " + e.getMessage());
        }
        return null;
    }

    public boolean save(Account account) {
        String sql = """
                INSERT INTO accounts (
                    id, client_id, account_type, balance, currency_code,
                    closed, opened_at, closed_at
                ) VALUES (?, ?, ?::account_type, ?, ?, ?, ?, ?)
                """;

        try (Connection cnx = this.connection.getConnection();
                PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setObject(1, account.getId());
            ps.setObject(2, account.getOwnerId());
            ps.setString(3, account.getType().name());
            ps.setBigDecimal(4, account.getBalance());
            ps.setString(5, account.getCurrency().getCode());
            ps.setBoolean(6, account.isClosed());
            ps.setObject(7, account.getOpenedAt());
            ps.setObject(8, account.getClosedAt());

            ps.executeUpdate();

            System.out.println("Compte inséré avec succès : " + account.getId());
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deposit(UUID id, BigDecimal balance) {
        Account acount = this.getAccountById(id);
        BigDecimal newBalance = acount.getBalance().add(balance);

        if (acount.getType().equals(Account.AccountType.CREDIT)){
            creditRepository.update(acount.getId(),newBalance);
        }
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?::uuid";
        try (Connection cnx = this.connection.getConnection();
                PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setBigDecimal(1, newBalance);
            ps.setObject(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean withdraw(UUID id, BigDecimal balance) {
        Account acount = this.getAccountById(id);
        BigDecimal amount = acount.getBalance();
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?::uuid";
        try (Connection cnx = this.connection.getConnection();
                PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount.subtract(balance));
            ps.setObject(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Client getClientByAccountId(UUID accountId) {
        String sql = "SELECT c.* FROM clients c JOIN accounts a ON c.id = a.client_id WHERE a.id = ?::uuid AND a.deleted = false";

        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Client client = new Client();
                client.setId((UUID) rs.getObject("id"));
                client.setUsername(rs.getString("username"));
                client.setFullName(rs.getString("full_name"));
                client.setNationalId(rs.getString("national_id"));
                client.setMonthlyIncome(rs.getBigDecimal("monthly_income"));
                client.setCurrency(Currency.fromCode(rs.getString("currency_code")));
                client.setEmail(rs.getString("email"));
                client.setPhone(rs.getString("phone"));
                client.setBirthDate(rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null);
                client.setActive(rs.getBoolean("active"));
                client.setRole(rs.getString("role"));
                client.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                client.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                client.setLastLoginAt(rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null);
                return client;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
        }
        return null;
    }
   
}
