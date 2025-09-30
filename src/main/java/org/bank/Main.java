package org.bank;

import org.bank.domain.Currency;
import org.bank.domain.Transaction;
import org.bank.service.TellerService;
import org.bank.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        
        // Test 1: Transfer interne (même client)
        System.out.println("=== TEST 1: TRANSFER INTERNE ===");
        // testTransferInterne();
        
        System.out.println("\n=== TEST 2: TRANSFER EXTERNE ===");
        testTransferExterne();
    }
    
    // private static void testTransferInterne() throws SQLException {
    //     TellerService teller = new TellerService();
        
    //     // Utiliser des comptes qui appartiennent au même client (JD987654321)
    //     UUID sourceAccount = UUID.fromString("1823acda-3145-48b0-8440-b3126a95834c"); // COURANT 2199.00 - JD987654321
    //     UUID targetAccount = UUID.fromString("2823acda-3145-48b0-8440-b3126a95834c"); // EPARGNE 25000.00 - JD987654321
        
    //     boolean result = teller.transferParTeller(
    //         UUID.randomUUID(),                           // transactionId
    //         Transaction.TransactionType.TRANSFER,        // type
    //         Transaction.TransactionStatus.SETTLED,       // status
    //         sourceAccount,                               // source
    //         targetAccount,                               // target
    //         BigDecimal.valueOf(500.00),               
    //         Currency.MAD,                                // currency
    //         UUID.fromString("880e8400-e29b-41d4-a716-446655440001"), // user
    //         "TEST-TRANSFER-001",                         // reference
    //         "Test transfert interne"                     // description
    //     );
        
    //     System.out.println("Résultat transfert interne: " + (result ? "SUCCÈS" : "ÉCHEC"));
    // }
    
    private static void testTransferExterne() throws SQLException {
        TellerService teller = new TellerService();
        UUID sourceAccount = UUID.fromString("1823acda-3145-48b0-8440-b3126a95834c"); // COURANT 2199.00 - JD987654321
        UUID targetAccount = UUID.fromString("1923acda-3145-48b0-8440-b3126a95834c"); // COURANT 6850.00 - MM456789123

        boolean result = teller.transferExterneParTeller(
            UUID.randomUUID(),                           // transactionId
            Transaction.TransactionType.TRANSFER_EXTERNAL, // type
            Transaction.TransactionStatus.SETTLED,       // status
            sourceAccount,                               // source
            targetAccount,                               // target
            BigDecimal.valueOf(300.00),                  
            Currency.MAD,                                // currency
            UUID.fromString("880e8400-e29b-41d4-a716-446655440001"), // user
            "TEST-EXTERNAL-001",                         // reference
            "Test transfert externe"                     // description
        );
        
        System.out.println("Résultat transfert externe: " + (result ? "SUCCÈS" : "ÉCHEC"));
    }
}