package org.bank.ui;

import org.bank.domain.Client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

public class UiTeller {

    public void showMenu(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez le username: ");
        String username = scanner.nextLine();

        System.out.print("Entrez le nom complet: ");
        String fullName = scanner.nextLine();

        System.out.print("Entrez l'email: ");
        String email = scanner.nextLine();

        System.out.print("Entrez le téléphone: ");
        String phone = scanner.nextLine();

        System.out.print("Entrez le National ID: ");
        String nationalId = scanner.nextLine();

        System.out.print("Entrez le revenu mensuel: ");
        BigDecimal monthlyIncome = new BigDecimal(scanner.nextLine());

        System.out.print("Entrez la date de naissance (format: yyyy-MM-dd): ");
        LocalDate birthDate = LocalDate.parse(scanner.nextLine());

        boolean active = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        scanner.close();
        Client client = new Client(UUID.randomUUID(),username,fullName,nationalId,monthlyIncome,email,phone,birthDate,active);


    }

}
