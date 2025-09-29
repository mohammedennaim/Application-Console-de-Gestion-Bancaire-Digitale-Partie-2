package org.bank.ui;

import org.bank.domain.Client;
import org.bank.domain.Currency;
import org.bank.service.TellerService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

public class UiTeller {
    TellerService tellerService = new TellerService();
    Scanner scanner = new Scanner(System.in);

    public UiTeller() throws SQLException {
    }

    public void showMenu(){
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n=== MENU TELLER ===");
            System.out.println("1. Créer un nouveau client");
            System.out.println("2. Créer un compte");
            System.out.println("3. Quitter");
            System.out.print("Choisissez une option: ");
            
            try {
                int choix = Integer.parseInt(scanner.nextLine());
                
                switch (choix) {
                    case 1:
                        creeClient();
                        break;
                    case 2:
                        System.out.println("Création de compte - À implémenter");
                        break;
                    case 3:
                        continuer = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Option invalide. Veuillez choisir 1, 2 ou 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur: Veuillez entrer un nombre valide.");
            }
        }
    }
    
    private void creeClient() {
        System.out.println("\n=== CRÉATION D'UN NOUVEAU CLIENT ===");
        
        try {
            System.out.print("Entrez le username: ");
            String username = scanner.nextLine().trim();
            
            if (username.isEmpty()) {
                System.out.println("Erreur: Le username ne peut pas être vide.");
                return;
            }

            System.out.print("Entrez le nom complet: ");
            String fullName = scanner.nextLine().trim();
            
            if (fullName.isEmpty()) {
                System.out.println("Erreur: Le nom complet ne peut pas être vide.");
                return;
            }

            System.out.print("Entrez l'email: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty() || !email.contains("@")) {
                System.out.println("Erreur: Veuillez entrer un email valide.");
                return;
            }

            System.out.print("Entrez le téléphone: ");
            String phone = scanner.nextLine().trim();

            System.out.print("Entrez le National ID: ");
            String nationalId = scanner.nextLine().trim();
            
            if (nationalId.isEmpty()) {
                System.out.println("Erreur: Le National ID ne peut pas être vide.");
                return;
            }

            System.out.print("Entrez le revenu mensuel: ");
            BigDecimal monthlyIncome;
            try {
                monthlyIncome = new BigDecimal(scanner.nextLine().trim());
                if (monthlyIncome.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Erreur: Le revenu mensuel ne peut pas être négatif.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur: Veuillez entrer un montant valide.");
                return;
            }

            System.out.print("Entrez la date de naissance (format: yyyy-MM-dd): ");
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(scanner.nextLine().trim());
                if (birthDate.isAfter(LocalDate.now())) {
                    System.out.println("Erreur: La date de naissance ne peut pas être dans le futur.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erreur: Veuillez entrer une date valide au format yyyy-MM-dd.");
                return;
            }

            boolean active = true;
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime updatedAt = LocalDateTime.now();

            Client client = new Client(UUID.randomUUID(), username, fullName, nationalId, monthlyIncome, Currency.MAD, email, phone, birthDate, active);
            client.setCreatedAt(createdAt);
            client.setUpdatedAt(updatedAt);
            client.setRole("CLIENT");
            
            tellerService.createClient(client);
            
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du client: " + e.getMessage());
        }
    }
    
    public void fermer() {
        if (scanner != null) {
            scanner.close();
        }
    }

}
