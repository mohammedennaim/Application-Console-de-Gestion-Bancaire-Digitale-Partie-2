package org.bank.controller;

import org.bank.domain.Account;
import org.bank.domain.Client;
import org.bank.domain.Currency;
import org.bank.domain.Transaction;
import org.bank.service.TellerService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TellerController {
    private TellerService tellerService;

    public TellerController() throws SQLException {
        this.tellerService = new TellerService();
    }

    public boolean createClient(String username, String fullName, String nationalId, 
                               BigDecimal monthlyIncome, String email, String phone, LocalDate birthDate) {
        try {
            Client client = new Client(
                UUID.randomUUID(),
                username,
                fullName,
                nationalId,
                monthlyIncome,
                Currency.MAD,
                email,
                phone,
                birthDate,
                true
            );
            
            return tellerService.createClient(client);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du client: " + e.getMessage());
            return false;
        }
    }

    public boolean updateClient(UUID clientId, String username, String fullName, String nationalId,
                               BigDecimal monthlyIncome, String email, String phone, LocalDate birthDate) {
        try {
            Client client = new Client(
                clientId,
                username,
                fullName,
                nationalId,
                monthlyIncome,
                Currency.MAD,
                email,
                phone,
                birthDate,
                true
            );
            
            return tellerService.updateClient(client);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du client: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteClient(UUID clientId) {
        try {
            Client client = new Client();
            client.setId(clientId);
            return tellerService.deleteClient(client);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du client: " + e.getMessage());
            return false;
        }
    }

    public boolean createAccount(UUID clientId, Account.AccountType accountType, BigDecimal initialBalance) {
        try {
            Account account = new Account(
                UUID.randomUUID(),
                clientId,
                accountType,
                initialBalance,
                Currency.MAD,
                LocalDateTime.now()
            );
            
            return tellerService.createAccount(account);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du compte: " + e.getMessage());
            return false;
        }
    }

    public boolean deposit(UUID accountId, BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Le montant doit être positif");
                return false;
            }
            return tellerService.depositParTeller(accountId, amount);
        } catch (Exception e) {
            System.err.println("Erreur lors du dépôt: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(UUID accountId, BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Le montant doit être positif");
                return false;
            }
            return tellerService.withdrawParTeller(accountId, amount);
        } catch (Exception e) {
            System.err.println("Erreur lors du retrait: " + e.getMessage());
            return false;
        }
    }

    public boolean transfer(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, 
                           UUID tellerId, String description) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Le montant doit être positif");
                return false;
            }
            
            // Récupérer l'ID du client propriétaire du compte source
            // pour l'utiliser comme initiated_by_user_id (au lieu du teller)
            UUID sourceClientId = tellerService.getClientIdByAccountId(sourceAccountId);
            if (sourceClientId == null) {
                System.err.println("Impossible de trouver le client propriétaire du compte source");
                return false;
            }
            
            return tellerService.transferByTeller(
                UUID.randomUUID(),
                Transaction.TransactionType.TRANSFER,
                Transaction.TransactionStatus.PENDING,
                sourceAccountId,
                targetAccountId,
                amount,
                Currency.MAD,
                sourceClientId, // Utiliser l'ID du client au lieu du teller
                "TELLER_" + System.currentTimeMillis(),
                description
            );
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert: " + e.getMessage());
            return false;
        }
    }

    public boolean transferExternal(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount,
                                   UUID tellerId, String description) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Le montant doit être positif");
                return false;
            }
            
            // Récupérer l'ID du client propriétaire du compte source
            // pour l'utiliser comme initiated_by_user_id (au lieu du teller)
            UUID sourceClientId = tellerService.getClientIdByAccountId(sourceAccountId);
            if (sourceClientId == null) {
                System.err.println("Impossible de trouver le client propriétaire du compte source");
                return false;
            }
            
            return tellerService.transferExterneByTeller(
                UUID.randomUUID(),
                Transaction.TransactionType.TRANSFER_EXTERNAL,
                Transaction.TransactionStatus.PENDING,
                sourceAccountId,
                targetAccountId,
                amount,
                Currency.MAD,
                sourceClientId, // Utiliser l'ID du client au lieu du teller
                "EXT_TELLER_" + System.currentTimeMillis(),
                description
            );
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert externe: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidUUID(String uuidString) {
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isValidBirthDate(LocalDate birthDate) {
        return birthDate != null && birthDate.isBefore(LocalDate.now());
    }
}
