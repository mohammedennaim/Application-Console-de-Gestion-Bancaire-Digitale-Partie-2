package org.bank.service;

import org.bank.domain.Account;
import org.bank.domain.Credit;
import org.bank.domain.Currency;
import org.bank.repository.implimentation.CreditRepositoryImpl;
import org.bank.repository.implimentation.ClientRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreditService {
    private final AccountService accountService;
    private final CreditRepositoryImpl creditImpl;
    private final ClientRepositoryImpl clientImpl;

    public CreditService() throws SQLException {
        this.accountService = new AccountService();
        this.creditImpl = new CreditRepositoryImpl();
        this.clientImpl = new ClientRepositoryImpl();
    }

    // public boolean createCredit(BigDecimal amount, UUID clientID, BigDecimal interestRate, Credit.InterestMode type) {
    //     try {
    //         if (!clientImpl.findById(clientID)) {
    //             System.err.println("Erreur: Le client avec l'ID " + clientID + " n'existe pas");
    //             return false;
    //         }

    //         if (amount.compareTo(clientImpl.getClientById(clientID).getMonthlyIncome().multiply(BigDecimal.valueOf(0.4))) > 0) {
    //             System.err.println("Erreur: Le montant pour un crédit dépasse 40% du salaire client");
    //             return false;
    //         }

    //         Account creditAccount = accountService.getCreditAccountByClientId(clientID);
            
    //         if (creditAccount == null) {
    //             creditAccount = new Account(
    //                 UUID.randomUUID(),clientID,Account.AccountType.CREDIT,BigDecimal.ZERO,Currency.MAD,LocalDateTime.now()
    //             );
                
    //             accountService.createAccount(creditAccount);
    //             System.out.println("Nouveau compte de crédit créé avec succès pour le client: " + clientID);
    //         }
    //         return true;

    //     } catch (Exception e) {
    //         System.err.println("Erreur inattendue lors de la création du crédit: " + e.getMessage());
    //         return false;
    //     }
    // }

    // /**
    //  * Méthode pour créer uniquement l'entrée crédit pour un compte existant
    //  * Utilisée par AccountService pour éviter la récursion infinie
    //  */
    public boolean createCreditEntry(UUID accountId, UUID clientId, BigDecimal interestRate, Credit.InterestMode type) {
        try {
            this.creditImpl.credit(BigDecimal.ZERO, clientId, accountId, interestRate, type);
            System.out.println("Entrée crédit créée avec succès pour le compte: " + accountId);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'entrée crédit: " + e.getMessage());
            return false;
        }
    }

    public void displayCreditInfo(BigDecimal amount, BigDecimal interestRate, Credit.InterestMode type) {
        System.out.println("\n=== SIMULATION DE CRÉDIT ===");
        System.out.println("Montant: " + amount + " MAD");
        System.out.println("Taux d'intérêt annuel: " + interestRate.multiply(BigDecimal.valueOf(100)) + "%");
        System.out.println("Mode de calcul: " + (type == Credit.InterestMode.SIMPLE ? "Intérêts simples" : "Intérêts composés"));

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
        BigDecimal totalInterest;
        
        if (type == Credit.InterestMode.SIMPLE) {
            totalInterest = amount.multiply(interestRate);
        } else {
            totalInterest = amount.multiply(monthlyRate).multiply(BigDecimal.valueOf(12));
        }
        
        BigDecimal totalAmount = amount.add(totalInterest);
        BigDecimal monthlyPayment = totalAmount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        
        System.out.println("Intérêts totaux (estimation): " + totalInterest + " MAD");
        System.out.println("Montant total à rembourser: " + totalAmount + " MAD");
        System.out.println("Mensualité approximative: " + monthlyPayment + " MAD");
        System.out.println("=============================\n");
    }
}
