package org.bank;

import org.bank.domain.Transaction;
import org.bank.repository.implimentation.TransactionRepositoryImpl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Test d'ajout de transaction ===");

        try {
            TransactionRepositoryImpl transactionRepo = new TransactionRepositoryImpl();

            UUID transactionId = UUID.randomUUID();
            UUID sourceAccountId = UUID.fromString("1823acda-3145-48b0-8440-b3126a95834c");
            UUID targetAccountId = UUID.fromString("2723acda-3145-48b0-8440-b3126a95834c");

            UUID initiatedByUserId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // Client existant (client.test)

            System.out.println("1. Création d'une transaction DEPOSIT...");

            boolean success = transactionRepo.ajouter(
                    transactionId,
                    Transaction.TransactionType.DEPOSIT,
                    Transaction.TransactionStatus.SETTLED,
                    sourceAccountId, // sourceAccountId - null pour un DEPOSIT
                    targetAccountId, // targetAccountId
                    new BigDecimal("500.00"),
                    new BigDecimal("1.50"), // frais
                    Currency.getInstance("USD"),
                    initiatedByUserId,
                    "TEST-REF-001",
                    "Test de dépôt"
            );

            if (success) {
                System.out.println("✅ Transaction ajoutée avec succès !");
                System.out.println("   ID: " + transactionId);
                System.out.println("   Type: DEPOSIT");
                System.out.println("   Montant: 1000.00 MAD");
                System.out.println("   Statut: SETTLED");
            } else {
                System.out.println("❌ Échec de l'ajout de la transaction");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation du repository ou de l'ajout :"+e.getMessage());
        }

        System.out.println("\n=== Test terminé ===");
    }
}