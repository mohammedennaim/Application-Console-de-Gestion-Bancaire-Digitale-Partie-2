package org.bank.service;

import org.bank.domain.Account;
import org.bank.domain.Credit;
import org.bank.domain.Currency;
import org.bank.repository.implimentation.CreditRepositoryImpl;
import org.bank.repository.implimentation.ClientRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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

    public boolean createCredit(BigDecimal amount, UUID clientID, BigDecimal interestRate, Credit.InterestMode type) {
        try {
            if (!clientImpl.findById(clientID)) {
                System.err.println("Erreur: Le client avec l'ID " + clientID + " n'existe pas");
                return false;
            }

            if (amount.compareTo(clientImpl.getClientById(clientID).getMonthlyIncome().multiply(BigDecimal.valueOf(0.04))) < 0) {
                System.err.println("Erreur: Le montant pour un crédit est dépaser 40% de saliare client");
                return false;
            }

            Account creditAccount = null;
            
            if (accountService.hasCreditAccount(clientID)) {
                creditAccount = accountService.getCreditAccountByClientId(clientID);
                System.out.println("Utilisation du compte de crédit existant: " + creditAccount.getId());
            } else {
                creditAccount = new Account();
                creditAccount.setId(UUID.randomUUID());
                creditAccount.setOwnerId(clientID);
                creditAccount.setBalance(BigDecimal.ZERO);
                creditAccount.setBankCode("BANK001");
                creditAccount.setType(Account.AccountType.CREDIT);
                creditAccount.setCurrency(Currency.MAD);
                creditAccount.setOverdraftAllowed(false);
                creditAccount.setOverdraftLimit(BigDecimal.ZERO);

                if (accountService.createAccount(creditAccount)) {
                    System.out.println("Nouveau compte de crédit créé avec succès pour le client: " + clientID);
                } else {
                    System.err.println("Erreur lors de la création du compte de crédit");
                    return false;
                }
            }

            boolean creditCreated = creditImpl.credit(amount, clientID, creditAccount.getId(), interestRate, type);
            
            if (creditCreated) {
                System.out.println("✅ Demande de crédit créée avec succès!");
                System.out.println("📋 Détails du crédit:");
                System.out.println("   - Montant demandé: " + amount + " MAD");
                System.out.println("   - Taux d'intérêt: " + interestRate.multiply(BigDecimal.valueOf(100)) + "%");
                System.out.println("   - Mode d'intérêt: " + type);
                System.out.println("   - Statut: EN ATTENTE D'APPROBATION");
                System.out.println("   - Client ID: " + clientID);
                return true;
            } else {
                System.err.println("Erreur lors de la création de la demande de crédit");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la création du crédit: " + e.getMessage());
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
