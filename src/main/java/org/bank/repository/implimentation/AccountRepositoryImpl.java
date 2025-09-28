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


    @Override
    public boolean findById(UUID id) {
        return getAccountById(id) != null;
    }
    
    // Méthode utilitaire pour récupérer l'objet Account complet
    public Account getAccountById(UUID id) {
        if (id == null) {
            System.err.println("Erreur: ID ne peut pas être null");
            return null;
        }
        
        Connection cnx = this.connection.getConnection();
        String sql = "SELECT * FROM accounts WHERE id::text = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, id.toString());
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
            System.err.println("Erreur lors de recherche de ID " + id + ": " + e.getMessage());
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

    @Override
    public boolean delete(UUID id){
        if (id == null) {
            System.err.println("Erreur: ID ne peut pas être null");
            return false;
        }
        
        // Vérifier que le compte existe avant de le supprimer
        if (!findById(id)) {
            System.out.println("Aucun compte trouvé avec l'ID: " + id);
            return false;
        }
        
        Connection cnx = this.connection.getConnection();
        
        try {
            // Commencer une transaction
            cnx.setAutoCommit(false);
            
            // 1. Vérifier les références dans les transactions
            String checkTransactionsSql = "SELECT COUNT(*) FROM transactions WHERE source_account_id = ? OR target_account_id = ?";
            int transactionCount = 0;
            try (PreparedStatement checkPs = cnx.prepareStatement(checkTransactionsSql)) {
                checkPs.setObject(1, id);
                checkPs.setObject(2, id);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    transactionCount = rs.getInt(1);
                }
            }
            
            // 2. Vérifier les références dans les crédits
            String checkCreditsSql = "SELECT COUNT(*) FROM credits WHERE linked_account_id = ?";
            int creditCount = 0;
            try (PreparedStatement checkPs = cnx.prepareStatement(checkCreditsSql)) {
                checkPs.setObject(1, id);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    creditCount = rs.getInt(1);
                }
            }
            
            // Informer l'utilisateur des dépendances
            if (transactionCount > 0) {
                System.out.println("AVERTISSEMENT: Ce compte a " + transactionCount + " transaction(s) associée(s)");
            }
            if (creditCount > 0) {
                System.out.println("AVERTISSEMENT: Ce compte a " + creditCount + " crédit(s) associé(s)");
            }
            
            // 3. Supprimer les transactions liées (optionnel - comportement en cascade)
            if (transactionCount > 0) {
                String deleteTransactionsSql = "DELETE FROM transactions WHERE source_account_id = ? OR target_account_id = ?";
                try (PreparedStatement deleteTransPs = cnx.prepareStatement(deleteTransactionsSql)) {
                    deleteTransPs.setObject(1, id);
                    deleteTransPs.setObject(2, id);
                    int deletedTransactions = deleteTransPs.executeUpdate();
                    System.out.println("→ " + deletedTransactions + " transaction(s) supprimée(s)");
                }
            }
            
            // 4. Supprimer les crédits liés
            if (creditCount > 0) {
                String deleteCreditsSql = "DELETE FROM credits WHERE linked_account_id = ?";
                try (PreparedStatement deleteCreditPs = cnx.prepareStatement(deleteCreditsSql)) {
                    deleteCreditPs.setObject(1, id);
                    int deletedCredits = deleteCreditPs.executeUpdate();
                    System.out.println("→ " + deletedCredits + " crédit(s) supprimé(s)");
                }
            }
            
            // 5. Supprimer le compte principal
            String deleteAccountSql = "DELETE FROM accounts WHERE id = ?";
            try (PreparedStatement deleteAccountPs = cnx.prepareStatement(deleteAccountSql)) {
                deleteAccountPs.setObject(1, id);
                int rowsAffected = deleteAccountPs.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Valider la transaction
                    cnx.commit();
                    System.out.println("✅ Compte et toutes ses dépendances supprimés avec succès: " + id);
                    return true;
                } else {
                    // Annuler la transaction
                    cnx.rollback();
                    System.out.println("❌ Échec de la suppression du compte");
                    return false;
                }
            }
            
        } catch (SQLException e) {
            // Annuler la transaction en cas d'erreur
            try {
                cnx.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors du rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Erreur lors de la suppression du compte: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Restaurer l'auto-commit
            try {
                cnx.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la restauration de l'auto-commit: " + e.getMessage());
            }
        }
    }

}
