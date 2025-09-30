package org.bank;

import org.bank.domain.Currency;
import org.bank.domain.Transaction;
import org.bank.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        TransactionService transaction = new TransactionService();
         transaction.transfer(UUID.randomUUID(),
                Transaction.TransactionType.TRANSFER,
                Transaction.TransactionStatus.SETTLED,
                 UUID.fromString("1923acda-3145-48b0-8440-b3126a95834c"),
                 UUID.fromString("1823acda-3145-48b0-8440-b3126a95834c"),
                BigDecimal.valueOf(100.50),
                BigDecimal.valueOf(0.5),
                Currency.MAD,
                UUID.fromString("880e8400-e29b-41d4-a716-446655440001"),
                "TEST-REF-001",
                "Test test test test");
    }
}