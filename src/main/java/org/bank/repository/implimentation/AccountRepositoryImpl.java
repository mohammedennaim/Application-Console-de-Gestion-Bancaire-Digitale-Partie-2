package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Account;
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

    public AccountRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }
    
    public Account getAccountById(UUID id) {
        if (id == null) {
            System.err.println("Erreur: ID ne peut pas être null");
            return null;
        }
        
        Connection cnx = this.connection.getConnection();
        String sql = "SELECT * FROM accounts WHERE id::text = ? AND (deleted = false OR deleted IS NULL)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setId((UUID) rs.getObject("id"));
                account.setOwnerId((UUID) rs.getObject("client_id"));
                account.setType(Account.AccountType.valueOf(rs.getString("account_type")));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCurrency(Currency.fromCode(rs.getString("currency_code")));
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
            System.err.println("Erreur lors de recherche de ID " + id + ": " + e.getMessage());
        }
        return null;
    }
    
    public boolean findById(UUID id) {
        return getAccountById(id) != null;
    }

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
            ps.setString(5, account.getCurrency().getCode());
            ps.setBoolean(6, account.isOverdraftAllowed());
            ps.setBigDecimal(7, account.getOverdraftLimit());
            ps.setBoolean(8, account.isClosed());
            ps.setObject(9, account.getOpenedAt());
            ps.setObject(10, account.getClosedAt());

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
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?::uuid";
        try (Connection cnx = this.connection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setBigDecimal(1, balance);
            ps.setObject(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean withdraw(UUID id, BigDecimal balance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?::uuid";
        try (Connection cnx = this.connection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setBigDecimal(1, balance);
            ps.setObject(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean delete(UUID id){
        if (id == null) {
            System.err.println("Erreur: ID ne peut pas être null");
            return false;
        }
        
        if (!findById(id)) {
            System.out.println("Aucun compte trouvé avec l'ID: " + id);
            return false;
        }
        
        Connection cnx = this.connection.getConnection();
        
        try {
            String softDeleteSql = """
            UPDATE accounts SET deleted = true, deleted_at = CURRENT_TIMESTAMP,updated_at = CURRENT_TIMESTAMP
            WHERE id = ?::uuid AND deleted = false
            """;
            
            try (PreparedStatement ps = cnx.prepareStatement(softDeleteSql)) {
                ps.setString(1, id.toString());
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Compte marqué comme supprimé (soft delete) : " + id);
                    System.out.println("   → Les données sont préservées pour l'historique");
                    return true;
                } else {
                    System.out.println("Le compte est déjà marqué comme supprimé : " + id);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du compte: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(UUID id) {
        Connection cnx = this.connection.getConnection();


        return false;
    }

    public boolean restore(UUID id) {
        if (id == null) {
            System.err.println("Erreur: ID ne peut pas être null");
            return false;
        }
        
        Connection cnx = this.connection.getConnection();
        
        try {
            String restoreSql = """
            UPDATE accounts SET deleted = false, deleted_at = NULL, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?::uuid AND deleted = true
            """;
            
            try (PreparedStatement ps = cnx.prepareStatement(restoreSql)) {
                ps.setString(1, id.toString());
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Compte restauré avec succès");
                    return true;
                } else {
                    System.out.println("Aucun compte supprimé trouvé avec l'ID: " + id);
                    return false;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la restauration du compte: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBalance(BigDecimal amount,UUID id) {
        try {
            DatabaseConnection cnx = DatabaseConnection.getInstance();
            String sql = "UPDATE accounts SET balance = ? WHERE id = ?::uuid;";
            PreparedStatement ps = cnx.getConnection().prepareStatement(sql);
            ps.setBigDecimal(1,amount);
            ps.setObject(1, id.toString());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("balance update avec succès");
                return true;
            } else {
                System.out.println("Aucun compte trouvé avec l'ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Vérifie si un compte existe
     * @param accountId ID du compte
     * @return true si le compte existe
     */
    @Override
    public boolean accountExists(UUID accountId) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE id = ?::uuid AND deleted = false";
        
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId.toString());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification d'existence du compte: " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupère l'ID du client propriétaire d'un compte
     * @param accountId ID du compte
     * @return l'ID du client ou null si non trouvé
     */
    @Override
    public UUID getClientIdByAccountId(UUID accountId) {
        String sql = "SELECT client_id FROM accounts WHERE id = ?::uuid AND deleted = false";
        
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId.toString());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return (UUID) rs.getObject("client_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client ID: " + e.getMessage());
        }
        return null;
    }
}
