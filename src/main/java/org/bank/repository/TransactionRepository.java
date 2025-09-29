package org.bank.repository;

import org.bank.domain.Transaction;

import java.math.BigDecimal;
import org.bank.domain.Currency;
import java.util.UUID;

public interface TransactionRepository {

    boolean transfer(UUID transactionId,
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
    );

    boolean transferExterne(
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
    );
}
