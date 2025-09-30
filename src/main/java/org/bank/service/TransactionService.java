package org.bank.service;

import org.bank.domain.Transaction;
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

    public TransactionService() throws SQLException {
    }


    private boolean validateTransfer(UUID sourceAccountId, UUID targetAccountId) {
        if (sourceAccountId == null || targetAccountId == null) {
            System.err.println("Erreur: Les IDs des comptes ne peuvent pas être null");
            return false;
        }

        if (sourceAccountId.equals(targetAccountId)) {
            System.err.println("Erreur: Le compte source et le compte cible ne peuvent pas être identiques");
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

        UUID sourceClientId = account.getClientIdByAccountId(sourceAccountId);
        UUID targetClientId = account.getClientIdByAccountId(targetAccountId);

        if (sourceClientId == null || targetClientId == null) {
            System.err.println("Erreur: Impossible de récupérer les propriétaires des comptes");
            return false;
        }

        // 5. Récupérer et comparer les nationalId
        String sourceNationalId = client.getNationalIdByClientId(sourceClientId);
        String targetNationalId = client.getNationalIdByClientId(targetClientId);

        if (sourceNationalId == null || targetNationalId == null) {
            System.err.println("Erreur: Impossible de récupérer les nationalId des clients");
            return false;
        }

        if (sourceNationalId.equals(targetNationalId)) {
            System.err.println("Erreur: Transfert refusé - les comptes appartiennent au même client (nationalId: " + sourceNationalId + ")");
            return false;
        }
        
        return true;
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


        if (!validateTransfer(sourceAccountId, targetAccountId)) {
            return false;
        }

        if (account.getAccountById(sourceAccountId).getBalance().compareTo(amount) < 0){
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
        if (!validateTransfer(sourceAccountId, targetAccountId)) {
            return false;
        }
        if (account.getAccountById(sourceAccountId).getBalance().compareTo(amount.add(fee)) < 0){
            return false;
        }

        try {
            transaction.transferExterne(transactionId, transactionType, transactionStatus, sourceAccountId, 
                                      targetAccountId, amount, fee, currency, initiatedByUserId, 
                                      externalReference, description);
            account.deposit(targetAccountId,amount);
            account.withdraw(sourceAccountId,amount.add(fee));
            // System.out.println("Transfert externe effectué avec succès");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du transfert externe: " + e.getMessage());
            return false;
        }
    }
}
