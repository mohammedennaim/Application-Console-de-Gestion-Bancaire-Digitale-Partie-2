package org.bank.service;

import org.bank.domain.Transaction;
import org.bank.domain.Account;
import org.bank.repository.implimentation.AccountRepositoryImpl;
import org.bank.repository.implimentation.ClientRepositoryImpl;
import org.bank.repository.implimentation.TransactionRepositoryImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.bank.domain.Currency;
import java.util.UUID;

public class TransactionService {
    AccountRepositoryImpl account = new AccountRepositoryImpl();
    ClientRepositoryImpl client = new ClientRepositoryImpl();
    TransactionRepositoryImpl transaction = new TransactionRepositoryImpl();
    FeeRuleService fee_rule = new FeeRuleService();



    public TransactionService() throws SQLException {
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
                           String description)
    {
        UUID sourceClientId = account.getClientByAccountId(sourceAccountId).getId();
        UUID targetClientId = account.getClientByAccountId(targetAccountId).getId();
        
        if (sourceClientId == null) {
            System.err.println("Erreur: clientId source ne peut pas être null");
            return false;
        }
        if (targetClientId == null) {
            System.err.println("Erreur: clientId cible ne peut pas être null");
            return false;
        }
        
        String sourceNationalId = client.getNationalIdByClientId(sourceClientId);
        String targetNationalId = client.getNationalIdByClientId(targetClientId);
        
        if (sourceNationalId == null) {
            System.err.println("Erreur: nationalId source ne peut pas être null");
            return false;
        }
        if (targetNationalId == null) {
            System.err.println("Erreur: nationalId cible ne peut pas être null");
            return false;
        }

        if (!sourceNationalId.equals(targetNationalId)) {
            System.err.println("Erreur: Transfert refusé - les comptes non appartiennent au même client (nationalId: " + sourceNationalId + ")");
            return false;
        }
        if (!account.findById(sourceAccountId)) {
            System.err.println("Erreur: Le compte source avec l'ID " + sourceAccountId + " n'existe pas");
            return false;
        }

        if (!account.findById(targetAccountId)) {
            System.err.println("Erreur: Le compte cible avec l'ID " + targetAccountId + " n'existe pas");
            return false;
        }
        if (sourceAccountId.equals(targetAccountId)) {
            System.err.println("Erreur: Le compte source et le compte cible ne peuvent pas être identiques");
            return false;
        }

        Account sourceAccount = account.getAccountById(sourceAccountId);
        if (sourceAccount == null) {
            System.err.println("Erreur: Impossible de récupérer les détails du compte source");
            return false;
        }
        if (sourceAccount.getBalance().compareTo(amount) < 0){
            System.err.println("Erreur: Solde insuffisant pour effectuer le transfert");
            return false;
        }

        try {
            transaction.transfer(transactionId, transactionType, transactionStatus, sourceAccountId, 
                               targetAccountId, amount, fee, currency, initiatedByUserId, 
                               externalReference, description);

            account.deposit(targetAccountId,amount);
            account.withdraw(sourceAccountId,amount);
            System.out.println("Transfert effectué avec succès");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du transfert: " + e.getMessage());
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
                            String description)
    {
        UUID sourceClientId = account.getClientByAccountId(sourceAccountId).getId();
        UUID targetClientId = account.getClientByAccountId(targetAccountId).getId();
        
        if (sourceClientId == null) {
            System.err.println("Erreur: clientId source ne peut pas être null");
            return false;
        }
        if (targetClientId == null) {
            System.err.println("Erreur: clientId cible ne peut pas être null");
            return false;
        }
        
        String sourceNationalId = client.getNationalIdByClientId(sourceClientId);
        String targetNationalId = client.getNationalIdByClientId(targetClientId);
        
        if (sourceNationalId == null) {
            System.err.println("Erreur: nationalId source ne peut pas être null");
            return false;
        }
        if (targetNationalId == null) {
            System.err.println("Erreur: nationalId cible ne peut pas être null");
            return false;
        }

        if (sourceNationalId.equals(targetNationalId)) {
            System.err.println("Erreur: Transfert refusé - les comptes appartiennent au même client (nationalId: " + sourceNationalId + ")");
            return false;
        }
        if (!account.findById(sourceAccountId)) {
            System.err.println("Erreur: Le compte source avec l'ID " + sourceAccountId + " n'existe pas");
            return false;
        }
        if (!account.findById(targetAccountId)) {
            System.err.println("Erreur: Le compte cible avec l'ID " + targetAccountId + " n'existe pas");
            return false;
        }
        if (sourceAccountId.equals(targetAccountId)) {
            System.err.println("Erreur: Le compte source et le compte cible ne peuvent pas être identiques");
            return false;
        }
        
        Account sourceAccount = account.getAccountById(sourceAccountId);
        if (sourceAccount == null) {
            System.err.println("Erreur: Impossible de récupérer les détails du compte source");
            return false;
        }
        if (sourceAccount.getBalance().compareTo(amount.add(fee)) < 0){
            System.err.println("Erreur: Solde insuffisant pour effectuer le transfert");
            return false;
        }

        try {
            transaction.transferExterne(transactionId, transactionType, transactionStatus, sourceAccountId, 
                                      targetAccountId, amount, fee, currency, initiatedByUserId, 
                                      externalReference, description);
            account.deposit(targetAccountId,amount);
            System.out.println(fee);
            System.out.println(amount.add(fee));
            account.withdraw(sourceAccountId,amount.add(fee));
            fee_rule.addFeeRuleParTransaction(sourceAccountId,fee);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du transfert externe: " + e.getMessage());
            return false;
        }
    }
}
