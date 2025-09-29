package org.bank.service;

import org.bank.domain.Transaction;
import org.bank.repository.implimentation.AccountRepositoryImpl;
import org.bank.repository.implimentation.TransactionRepositoryImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.bank.domain.Currency;
import java.util.UUID;

public class TransactionService {
    AccountRepositoryImpl account = new AccountRepositoryImpl();
    TransactionRepositoryImpl transaction = new TransactionRepositoryImpl();

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
        if (account.findById(targetAccountId) && account.findById(sourceAccountId) ) {
            transaction.transfer(transactionId,transactionType,transactionStatus,sourceAccountId,targetAccountId,amount,fee,currency,initiatedByUserId,externalReference,description);
            account.updateBalance(amount,targetAccountId);
            return true;
        }
        return false;
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
        if (account.findById(targetAccountId) && account.findById(sourceAccountId) && !targetAccountId.equals(sourceAccountId)) {
            transaction.transferExterne(transactionId,transactionType,transactionStatus,sourceAccountId,targetAccountId,amount,fee,currency,initiatedByUserId,externalReference,description);
            account.updateBalance(amount,targetAccountId);
            return true;
        }
        return false;
    }
}
