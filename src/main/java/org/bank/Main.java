package org.bank;

import org.bank.domain.Credit;
import org.bank.service.CreditService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        CreditService creditService = new CreditService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("🏦 === SYSTÈME DE CRÉDIT BANCAIRE ===");
        System.out.println();

        // Menu principal
        while (true) {
            System.out.println("📋 MENU PRINCIPAL:");
            System.out.println("1. Simuler et demander un crédit");
            System.out.println("2. Crédit rapide (5000 MAD)");
            System.out.println("3. Crédit personnalisé");
            System.out.println("4. Quitter");
            System.out.print("Choisissez une option (1-4): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    simulateAndRequestCredit(creditService, scanner);
                    break;
                case "2":
                    quickCredit(creditService, scanner);
                    break;
                case "3":
                    customCredit(creditService, scanner);
                    break;
                case "4":
                    System.out.println("\n👋 Merci d'avoir utilisé notre système bancaire!");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Option invalide. Veuillez choisir entre 1 et 4.");
            }
            System.out.println("\n" + "=".repeat(50) + "\n");
        }
    }

    private static void simulateAndRequestCredit(CreditService creditService, Scanner scanner) throws SQLException {
        UUID clientId = UUID.fromString("d5b8f3e2-1c4e-4f3e-8f3e-1c4e4f3e8f3e");
        BigDecimal amount = new BigDecimal("5000");
        BigDecimal interestRate = new BigDecimal("0.08"); // 8% annuel
        Credit.InterestMode mode = Credit.InterestMode.SIMPLE;

        creditService.displayCreditInfo(amount, interestRate, mode);

        System.out.print("Voulez-vous créer cette demande de crédit ? (o/n): ");
        String response = scanner.nextLine().toLowerCase();

        if (response.equals("o") || response.equals("oui")) {
            processCredit(creditService, amount, clientId, interestRate, mode);
        } else {
            System.out.println("👋 Simulation annulée.");
        }
    }

    private static void quickCredit(CreditService creditService, Scanner scanner) throws SQLException {
        UUID clientId = UUID.fromString("d5b8f3e2-1c4e-4f3e-8f3e-1c4e4f3e8f3e");
        BigDecimal amount = new BigDecimal("5000");
        BigDecimal interestRate = new BigDecimal("0.07"); // 7% annuel
        Credit.InterestMode mode = Credit.InterestMode.SIMPLE;

        System.out.println("⚡ CRÉDIT RAPIDE PRÉ-APPROUVÉ:");
        System.out.println("💰 Montant: 5000 MAD");
        System.out.println("📊 Taux: 7% (taux préférentiel)");
        System.out.println("⏱️ Traitement immédiat");
        
        System.out.print("Confirmer la demande ? (o/n): ");
        String response = scanner.nextLine().toLowerCase();

        if (response.equals("o") || response.equals("oui")) {
            processCredit(creditService, amount, clientId, interestRate, mode);
        } else {
            System.out.println("👋 Demande annulée.");
        }
    }

    private static void customCredit(CreditService creditService, Scanner scanner) throws SQLException {
        UUID clientId = UUID.fromString("d5b8f3e2-1c4e-4f3e-8f3e-1c4e4f3e8f3e");
        
        System.out.println("🎛️ CRÉDIT PERSONNALISÉ:");
        
        // Saisie du montant
        BigDecimal amount;
        while (true) {
            System.out.print("💰 Montant souhaité (minimum 1000 MAD): ");
            try {
                amount = new BigDecimal(scanner.nextLine());
                if (amount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                    break;
                } else {
                    System.out.println("❌ Le montant minimum est de 1000 MAD.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un montant valide.");
            }
        }

        // Saisie du taux d'intérêt
        BigDecimal interestRate;
        while (true) {
            System.out.print("📊 Taux d'intérêt annuel (ex: 0.08 pour 8%): ");
            try {
                interestRate = new BigDecimal(scanner.nextLine());
                if (interestRate.compareTo(BigDecimal.ZERO) > 0 && interestRate.compareTo(BigDecimal.valueOf(0.5)) <= 0) {
                    break;
                } else {
                    System.out.println("❌ Le taux doit être entre 0% et 50%.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un taux valide.");
            }
        }

        // Mode d'intérêt
        System.out.println("⚙️ Mode de calcul:");
        System.out.println("1. Intérêts simples (recommandé)");
        System.out.println("2. Intérêts composés");
        System.out.print("Choisissez (1-2): ");
        
        Credit.InterestMode mode;
        String modeChoice = scanner.nextLine();
        if (modeChoice.equals("2")) {
            mode = Credit.InterestMode.COMPOUND;
        } else {
            mode = Credit.InterestMode.SIMPLE;
        }

        // Afficher la simulation
        creditService.displayCreditInfo(amount, interestRate, mode);

        System.out.print("Confirmer cette demande de crédit ? (o/n): ");
        String response = scanner.nextLine().toLowerCase();

        if (response.equals("o") || response.equals("oui")) {
            processCredit(creditService, amount, clientId, interestRate, mode);
        } else {
            System.out.println("👋 Demande annulée.");
        }
    }

    private static void processCredit(CreditService creditService, BigDecimal amount, UUID clientId, 
                                     BigDecimal interestRate, Credit.InterestMode mode) throws SQLException {
        System.out.println("\n⏳ Traitement de la demande de crédit...");
        
        boolean success = creditService.createCredit(amount, clientId, interestRate, mode);
        
        if (success) {
            System.out.println("\n✅ SUCCÈS: Votre demande de crédit a été soumise avec succès!");
            System.out.println("📞 Un conseiller vous contactera sous 48h pour finaliser votre dossier.");
            System.out.println("📄 Vous recevrez également un email de confirmation.");
        } else {
            System.out.println("\n❌ ÉCHEC: Impossible de traiter votre demande de crédit.");
            System.out.println("💡 Veuillez vérifier vos informations ou contacter votre conseiller.");
        }
    }
}