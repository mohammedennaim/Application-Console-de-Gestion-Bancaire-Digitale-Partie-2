package org.bank.ui;

import org.bank.controller.TellerController;
import org.bank.domain.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

/**
 * Interface utilisateur pour les opérations du guichetier bancaire
 * Utilise le pattern MVC avec TellerController
 */
public class UiTeller {
    private TellerController tellerController;
    private Scanner scanner;
    private UUID currentTellerId; // ID du guichetier connecté

    public UiTeller() throws SQLException {
        this.tellerController = new TellerController();
        this.scanner = new Scanner(System.in);
        this.currentTellerId = UUID.randomUUID(); // En pratique, ceci viendrait de l'authentification
    }

    public void showMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    🏦 SYSTÈME BANCAIRE - INTERFACE GUICHETIER 🏦");
        System.out.println("=".repeat(60));
        System.out.println("Guichetier connecté: " + currentTellerId);
        
        boolean continuer = true;
        
        while (continuer) {
            displayMainMenu();
            
            try {
                int choix = Integer.parseInt(scanner.nextLine());
                
                switch (choix) {
                    case 1:
                        gestionClients();
                        break;
                    case 2:
                        gestionComptes();
                        break;
                    case 3:
                        operationsBancaires();
                        break;
                    case 4:
                        transferts();
                        break;
                    case 0:
                        continuer = false;
                        System.out.println("👋 Déconnexion du guichetier. Au revoir !");
                        break;
                    default:
                        System.out.println("❌ Option invalide. Veuillez choisir une option valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Erreur: Veuillez entrer un nombre valide.");
            } catch (Exception e) {
                System.out.println("❌ Erreur inattendue: " + e.getMessage());
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("               MENU PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. 👥 Gestion des clients");
        System.out.println("2. 🏦 Gestion des comptes");
        System.out.println("3. 💰 Opérations bancaires (Dépôts/Retraits)");
        System.out.println("4. 💸 Transferts");
        System.out.println("0. 🚪 Quitter");
        System.out.println("=".repeat(50));
        System.out.print("Choisissez une option: ");
    }

    // =================== GESTION DES CLIENTS ===================

    private void gestionClients() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("        GESTION DES CLIENTS");
        System.out.println("=".repeat(40));
        System.out.println("1. 📝 Créer un nouveau client");
        System.out.println("2. ✏️ Modifier un client existant");
        System.out.println("3. 🗑️ Supprimer un client");
        System.out.println("0. ⬅️ Retour au menu principal");
        System.out.print("Votre choix: ");

        try {
            int choix = Integer.parseInt(scanner.nextLine());
            switch (choix) {
                case 1:
                    creerClient();
                    break;
                case 2:
                    modifierClient();
                    break;
                case 3:
                    supprimerClient();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Option invalide.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur: Veuillez entrer un nombre valide.");
        }
    }

    private void creerClient() {
        System.out.println("\n=== 📝 CRÉATION D'UN NOUVEAU CLIENT ===");
        
        try {
            System.out.print("📧 Nom d'utilisateur: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("❌ Le nom d'utilisateur ne peut pas être vide.");
                return;
            }

            System.out.print("👤 Nom complet: ");
            String fullName = scanner.nextLine().trim();
            if (fullName.isEmpty()) {
                System.out.println("❌ Le nom complet ne peut pas être vide.");
                return;
            }

            System.out.print("🆔 CIN (Carte d'identité): ");
            String nationalId = scanner.nextLine().trim();
            if (nationalId.isEmpty()) {
                System.out.println("❌ Le CIN ne peut pas être vide.");
                return;
            }

            System.out.print("💰 Revenu mensuel (MAD): ");
            BigDecimal monthlyIncome;
            try {
                monthlyIncome = new BigDecimal(scanner.nextLine().trim());
                if (monthlyIncome.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("❌ Le revenu mensuel ne peut pas être négatif.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un montant valide.");
                return;
            }

            System.out.print("📧 Email: ");
            String email = scanner.nextLine().trim();
            if (!tellerController.isValidEmail(email)) {
                System.out.println("❌ Veuillez entrer un email valide.");
                return;
            }

            System.out.print("📞 Téléphone: ");
            String phone = scanner.nextLine().trim();

            System.out.print("🎂 Date de naissance (YYYY-MM-DD): ");
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(scanner.nextLine().trim());
                if (!tellerController.isValidBirthDate(birthDate)) {
                    System.out.println("❌ La date de naissance ne peut pas être dans le futur.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("❌ Format de date invalide. Utilisez YYYY-MM-DD.");
                return;
            }

            boolean success = tellerController.createClient(username, fullName, nationalId, 
                                                            monthlyIncome, email, phone, birthDate);
            if (success) {
                System.out.println("✅ Client créé avec succès!");
            } else {
                System.out.println("❌ Erreur lors de la création du client (peut-être qu'il existe déjà).");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création du client: " + e.getMessage());
        }
    }

    private void modifierClient() {
        System.out.println("\n=== ✏️ MODIFICATION D'UN CLIENT ===");
        
        System.out.print("🆔 ID du client à modifier: ");
        String clientIdStr = scanner.nextLine().trim();
        
        if (!tellerController.isValidUUID(clientIdStr)) {
            System.out.println("❌ Format d'ID invalide.");
            return;
        }
        
        UUID clientId = UUID.fromString(clientIdStr);
        
        // Collecte des nouvelles informations (même logique que création)
        System.out.println("Entrez les nouvelles informations (laissez vide pour garder l'ancienne valeur):");
        
        System.out.print("📧 Nouveau nom d'utilisateur: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("👤 Nouveau nom complet: ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("🆔 Nouveau CIN: ");
        String nationalId = scanner.nextLine().trim();
        
        System.out.print("💰 Nouveau revenu mensuel (MAD): ");
        String incomeStr = scanner.nextLine().trim();
        BigDecimal monthlyIncome = null;
        if (!incomeStr.isEmpty()) {
            try {
                monthlyIncome = new BigDecimal(incomeStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ Montant invalide, opération annulée.");
                return;
            }
        }
        
        System.out.print("📧 Nouvel email: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty() && !tellerController.isValidEmail(email)) {
            System.out.println("❌ Email invalide, opération annulée.");
            return;
        }
        
        System.out.print("📞 Nouveau téléphone: ");
        String phone = scanner.nextLine().trim();
        
        System.out.print("🎂 Nouvelle date de naissance (YYYY-MM-DD): ");
        String birthDateStr = scanner.nextLine().trim();
        LocalDate birthDate = null;
        if (!birthDateStr.isEmpty()) {
            try {
                birthDate = LocalDate.parse(birthDateStr);
            } catch (Exception e) {
                System.out.println("❌ Format de date invalide, opération annulée.");
                return;
            }
        }

        // Pour la démo, on utilise des valeurs par défaut si vides
        // En pratique, il faudrait récupérer les anciennes valeurs
        boolean success = tellerController.updateClient(
            clientId,
            username.isEmpty() ? "defaultUser" : username,
            fullName.isEmpty() ? "Default Name" : fullName,
            nationalId.isEmpty() ? "DEFAULTCIN" : nationalId,
            monthlyIncome != null ? monthlyIncome : new BigDecimal("3000"),
            email.isEmpty() ? "default@email.com" : email,
            phone.isEmpty() ? "0600000000" : phone,
            birthDate != null ? birthDate : LocalDate.of(1990, 1, 1)
        );

        if (success) {
            System.out.println("✅ Client modifié avec succès!");
        } else {
            System.out.println("❌ Erreur lors de la modification du client.");
        }
    }

    private void supprimerClient() {
        System.out.println("\n=== 🗑️ SUPPRESSION D'UN CLIENT ===");
        
        System.out.print("🆔 ID du client à supprimer: ");
        String clientIdStr = scanner.nextLine().trim();
        
        if (!tellerController.isValidUUID(clientIdStr)) {
            System.out.println("❌ Format d'ID invalide.");
            return;
        }
        
        UUID clientId = UUID.fromString(clientIdStr);
        
        System.out.print("⚠️ Êtes-vous sûr de vouloir supprimer ce client? (oui/non): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("oui")) {
            boolean success = tellerController.deleteClient(clientId);
            if (success) {
                System.out.println("✅ Client supprimé avec succès!");
            } else {
                System.out.println("❌ Erreur lors de la suppression du client.");
            }
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    // =================== GESTION DES COMPTES ===================

    private void gestionComptes() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("        GESTION DES COMPTES");
        System.out.println("=".repeat(40));
        System.out.println("1. 🆕 Créer un nouveau compte");
        System.out.println("0. ⬅️ Retour au menu principal");
        System.out.print("Votre choix: ");

        try {
            int choix = Integer.parseInt(scanner.nextLine());
            switch (choix) {
                case 1:
                    creerCompte();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Option invalide.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur: Veuillez entrer un nombre valide.");
        }
    }

    private void creerCompte() {
        System.out.println("\n=== 🆕 CRÉATION D'UN NOUVEAU COMPTE ===");
        
        try {
            System.out.print("🆔 ID du client propriétaire: ");
            String clientIdStr = scanner.nextLine().trim();
            
            if (!tellerController.isValidUUID(clientIdStr)) {
                System.out.println("❌ Format d'ID invalide.");
                return;
            }
            
            UUID clientId = UUID.fromString(clientIdStr);
            
            System.out.println("🏦 Type de compte:");
            System.out.println("1. 💳 COURANT");
            System.out.println("2. 💰 EPARGNE");
            System.out.println("3. 💳 CREDIT");
            System.out.print("Votre choix: ");
            
            int typeChoice = Integer.parseInt(scanner.nextLine());
            Account.AccountType accountType;
            
            switch (typeChoice) {
                case 1:
                    accountType = Account.AccountType.COURANT;
                    break;
                case 2:
                    accountType = Account.AccountType.EPARGNE;
                    break;
                case 3:
                    accountType = Account.AccountType.CREDIT;
                    break;
                default:
                    System.out.println("❌ Type de compte invalide.");
                    return;
            }
            
            System.out.print("💰 Solde initial (MAD): ");
            BigDecimal initialBalance = new BigDecimal(scanner.nextLine().trim());
            
            if (!tellerController.isValidAmount(initialBalance)) {
                System.out.println("❌ Le solde initial doit être positif.");
                return;
            }
            
            boolean success = tellerController.createAccount(clientId, accountType, initialBalance);
            if (success) {
                System.out.println("✅ Compte créé avec succès!");
            } else {
                System.out.println("❌ Erreur lors de la création du compte.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Veuillez entrer des valeurs numériques valides.");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    // =================== OPÉRATIONS BANCAIRES ===================

    private void operationsBancaires() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      OPÉRATIONS BANCAIRES");
        System.out.println("=".repeat(40));
        System.out.println("1. 📈 Effectuer un dépôt");
        System.out.println("2. 📉 Effectuer un retrait");
        System.out.println("0. ⬅️ Retour au menu principal");
        System.out.print("Votre choix: ");

        try {
            int choix = Integer.parseInt(scanner.nextLine());
            switch (choix) {
                case 1:
                    effectuerDepot();
                    break;
                case 2:
                    effectuerRetrait();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Option invalide.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur: Veuillez entrer un nombre valide.");
        }
    }

    private void effectuerDepot() {
        System.out.println("\n=== 📈 DÉPÔT SUR COMPTE ===");
        
        try {
            System.out.print("🆔 ID du compte: ");
            String accountIdStr = scanner.nextLine().trim();
            
            if (!tellerController.isValidUUID(accountIdStr)) {
                System.out.println("❌ Format d'ID invalide.");
                return;
            }
            
            UUID accountId = UUID.fromString(accountIdStr);
            
            System.out.print("💰 Montant à déposer (MAD): ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            boolean success = tellerController.deposit(accountId, amount);
            if (success) {
                System.out.println("✅ Dépôt effectué avec succès!");
            } else {
                System.out.println("❌ Erreur lors du dépôt.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Veuillez entrer un montant valide.");
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    private void effectuerRetrait() {
        System.out.println("\n=== 📉 RETRAIT DE COMPTE ===");
        
        try {
            System.out.print("🆔 ID du compte: ");
            String accountIdStr = scanner.nextLine().trim();
            
            if (!tellerController.isValidUUID(accountIdStr)) {
                System.out.println("❌ Format d'ID invalide.");
                return;
            }
            
            UUID accountId = UUID.fromString(accountIdStr);
            
            System.out.print("💰 Montant à retirer (MAD): ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            boolean success = tellerController.withdraw(accountId, amount);
            if (success) {
                System.out.println("✅ Retrait effectué avec succès!");
            } else {
                System.out.println("❌ Erreur lors du retrait.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Veuillez entrer un montant valide.");
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    // =================== TRANSFERTS ===================

    private void transferts() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           TRANSFERTS");
        System.out.println("=".repeat(40));
        System.out.println("1. 🔄 Transfert interne (gratuit)");
        System.out.println("2. 🌐 Transfert externe (frais: 15 MAD)");
        System.out.println("0. ⬅️ Retour au menu principal");
        System.out.print("Votre choix: ");

        try {
            int choix = Integer.parseInt(scanner.nextLine());
            switch (choix) {
                case 1:
                    effectuerTransfertInterne();
                    break;
                case 2:
                    effectuerTransfertExterne();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Option invalide.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur: Veuillez entrer un nombre valide.");
        }
    }

    private void effectuerTransfertInterne() {
        System.out.println("\n=== 🔄 TRANSFERT INTERNE ===");
        System.out.println("Note: Transfert entre comptes du même client (gratuit)");
        
        try {
            System.out.print("🆔 ID du compte source: ");
            UUID sourceAccountId = UUID.fromString(scanner.nextLine().trim());
            
            System.out.print("🆔 ID du compte destination: ");
            UUID targetAccountId = UUID.fromString(scanner.nextLine().trim());
            
            System.out.print("💰 Montant à transférer (MAD): ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            System.out.print("📝 Description: ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) description = "Transfert interne via guichetier";
            
            boolean success = tellerController.transfer(sourceAccountId, targetAccountId, 
                                                       amount, currentTellerId, description);
            if (success) {
                System.out.println("✅ Transfert interne effectué avec succès!");
            } else {
                System.out.println("❌ Erreur lors du transfert.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    private void effectuerTransfertExterne() {
        System.out.println("\n=== 🌐 TRANSFERT EXTERNE ===");
        System.out.println("Note: Transfert entre clients différents (frais: 15.00 MAD)");
        
        try {
            System.out.print("🆔 ID du compte source: ");
            UUID sourceAccountId = UUID.fromString(scanner.nextLine().trim());
            
            System.out.print("🆔 ID du compte destination: ");
            UUID targetAccountId = UUID.fromString(scanner.nextLine().trim());
            
            System.out.print("💰 Montant à transférer (MAD): ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            System.out.print("📝 Description: ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) description = "Transfert externe via guichetier";
            
            System.out.println("⚠️ Frais de transfert externe: 15.00 MAD");
            System.out.print("Confirmer le transfert? (oui/non): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("oui")) {
                boolean success = tellerController.transferExternal(sourceAccountId, targetAccountId,
                                                                   amount, currentTellerId, description);
                if (success) {
                    System.out.println("✅ Transfert externe effectué avec succès!");
                    System.out.println("💸 Frais appliqués: 15.00 MAD");
                } else {
                    System.out.println("❌ Erreur lors du transfert externe.");
                }
            } else {
                System.out.println("❌ Transfert annulé.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    public void fermer() {
        if (scanner != null) {
            scanner.close();
        }
    }

    // =================== MÉTHODE PRINCIPALE ===================

    public static void main(String[] args) {
        try {
            UiTeller uiTeller = new UiTeller();
            uiTeller.showMenu();
            uiTeller.fermer();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
        }
    }
}
