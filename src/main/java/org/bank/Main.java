package org.bank;

import org.bank.repository.implimentation.AccountRepositoryImpl;
import org.bank.repository.implimentation.ClientRepositoryImpl;

import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        AccountRepositoryImpl accountRepo = new AccountRepositoryImpl();
        System.out.println("=== COMPTE ===");
        System.out.println(accountRepo.findById(UUID.fromString("2723acda-3145-48b0-8440-b3126a95834c")));

//        ClientRepositoryImpl clientRepo = new ClientRepositoryImpl();
//        System.out.println("\n=== CLIENT ===");
//        System.out.println(clientRepo.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")));
    }
}