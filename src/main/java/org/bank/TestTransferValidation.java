package org.bank;

import org.bank.service.TransferValidationService;
import org.bank.service.TransferValidationService.TransferValidationResult;
import org.bank.repository.implimentation.AccountRepositoryImpl;
import org.bank.repository.implimentation.ClientRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestTransferValidation {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test de Validation des Transferts ===\n");
            
            testTransferValidation();
            
        } catch (Exception e) {
            System.err.println("Erreur lors des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testTransferValidation() throws SQLException {
        System.out.println("--- Test TransferValidationService ---");
        
        TransferValidationService validationService = new TransferValidationService();
        AccountRepositoryImpl accountRepo = new AccountRepositoryImpl();
        ClientRepositoryImpl clientRepo = new ClientRepositoryImpl();
        
        // Récupérer quelques comptes existants pour les tests
        List<UUID> accountIds = getExistingAccountIds();
        
        if (accountIds.size() < 2) {
            System.out.println("Pas assez de comptes dans la base pour effectuer les tests (minimum 2 requis)");
            return;
        }
        
        UUID firstAccountId = accountIds.get(0);
        UUID secondAccountId = accountIds.get(1);
        
        System.out.println("Comptes utilisés pour les tests:");
        System.out.println("- Compte 1: " + firstAccountId);
        System.out.println("- Compte 2: " + secondAccountId);
        System.out.println();
        
        // Test 1: Validation avec deux comptes différents
        System.out.println("1. Test validation avec deux comptes différents:");
        TransferValidationResult result1 = validationService.validateTransfer(firstAccountId, secondAccountId);
        System.out.println("   Résultat: " + result1);
        System.out.println();
        
        // Test 2: Validation avec le même compte (doit échouer)
        System.out.println("2. Test validation avec le même compte (doit échouer):");
        TransferValidationResult result2 = validationService.validateTransfer(firstAccountId, firstAccountId);
        System.out.println("   Résultat: " + result2);
        System.out.println();
        
        // Test 3: Validation avec compte source inexistant
        System.out.println("3. Test validation avec compte source inexistant:");
        UUID fakeAccountId = UUID.randomUUID();
        TransferValidationResult result3 = validationService.validateTransfer(fakeAccountId, secondAccountId);
        System.out.println("   Résultat: " + result3);
        System.out.println();
        
        // Test 4: Validation avec compte cible inexistant
        System.out.println("4. Test validation avec compte cible inexistant:");
        TransferValidationResult result4 = validationService.validateTransfer(firstAccountId, fakeAccountId);
        System.out.println("   Résultat: " + result4);
        System.out.println();
        
        // Test 5: Validation avec null
        System.out.println("5. Test validation avec null:");
        TransferValidationResult result5 = validationService.validateTransfer(null, secondAccountId);
        System.out.println("   Résultat: " + result5);
        System.out.println();
        
        // Test 6: Afficher les informations détaillées des comptes utilisés
        System.out.println("6. Informations détaillées des comptes:");
        displayAccountInfo(accountRepo, clientRepo, firstAccountId);
        displayAccountInfo(accountRepo, clientRepo, secondAccountId);
        
        // Test 7: Tester avec tous les comptes disponibles (pour voir les différents nationalId)
        if (accountIds.size() > 2) {
            System.out.println("\n7. Test avec tous les comptes disponibles:");
            for (int i = 0; i < Math.min(accountIds.size(), 5); i++) {
                for (int j = i + 1; j < Math.min(accountIds.size(), 5); j++) {
                    UUID sourceId = accountIds.get(i);
                    UUID targetId = accountIds.get(j);
                    TransferValidationResult result = validationService.validateTransfer(sourceId, targetId);
                    System.out.println("   " + sourceId.toString().substring(0, 8) + "... → " + 
                                     targetId.toString().substring(0, 8) + "... : " + 
                                     (result.isValid() ? "✓ VALIDE" : "✗ " + result.getErrorMessage()));
                }
            }
        }
    }
    
    private static List<UUID> getExistingAccountIds() throws SQLException {
        List<UUID> accountIds = new ArrayList<>();
        AccountRepositoryImpl accountRepo = new AccountRepositoryImpl();
        
        try {
            // Utiliser la connexion pour récupérer les IDs directement
            Connection cnx = accountRepo.connection.getConnection();
            String sql = "SELECT id FROM accounts WHERE deleted = false LIMIT 10";
            
            try (PreparedStatement ps = cnx.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    accountIds.add((UUID) rs.getObject("id"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des comptes: " + e.getMessage());
        }
        
        return accountIds;
    }
    
    private static void displayAccountInfo(AccountRepositoryImpl accountRepo, ClientRepositoryImpl clientRepo, 
                                         UUID accountId) {
        try {
            UUID clientId = accountRepo.getClientIdByAccountId(accountId);
            String nationalId = clientRepo.getNationalIdByClientId(clientId);
            
            System.out.println("   Compte " + accountId.toString().substring(0, 8) + "... → " +
                             "Client " + (clientId != null ? clientId.toString().substring(0, 8) + "..." : "null") + 
                             " → NationalId: " + nationalId);
        } catch (Exception e) {
            System.err.println("   Erreur lors de l'affichage des infos du compte " + accountId + ": " + e.getMessage());
        }
    }
}