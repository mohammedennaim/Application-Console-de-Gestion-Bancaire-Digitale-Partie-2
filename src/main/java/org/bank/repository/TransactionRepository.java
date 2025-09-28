package org.bank.repository;

import org.bank.domain.Transaction;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public interface TransactionRepository {

    public boolean ajouter(UUID transactionId,
                           Transaction.TransactionType transactionType,
                           Transaction.TransactionStatus transactionStatus,
                           UUID sourceAccountId,
                           UUID targetAccountId,
                           BigDecimal amount,
                           BigDecimal fee,
                           Currency currency,
                           UUID initiatedByUserId,
                           String externalReference,
                           String description);
    public boolean retirer();
    public boolean transferIn();
    public boolean transferOut();
}
