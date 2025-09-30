package org.bank.service;

import org.bank.domain.Account;
import org.bank.repository.implimentation.AccountRepositoryImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountService {
    AccountRepositoryImpl accountImpl = new AccountRepositoryImpl();

    public AccountService() throws SQLException {
    }

    public boolean findAccount(Account account){
        return accountImpl.findById(UUID.fromString(account.getId().toString()));
    }

    public boolean createAccount(Account account){
        if (!accountImpl.findById(UUID.fromString(account.getId().toString()))){
            // System.out.println("cette account il deja exist");
            return false;
        }
        accountImpl.save(account);
        // System.out.println("Creation de account is success");
        return true;
    }

    public boolean deleteAccount(Account account){
        if (accountImpl.findById(UUID.fromString(account.getId().toString()))){
            accountImpl.delete(UUID.fromString(account.getId().toString()));
            // System.out.println("Suppression de account is success");
            return true;
        }
        // System.out.println("cette account il n'exist pas");
        return false;
    }

    public boolean depositAccount(UUID accountId, BigDecimal amount) {
        if (!accountImpl.findById(accountId)) {
            return false;
        }

        Account account = accountImpl.getAccountById(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        return accountImpl.deposit(accountId, newBalance);
    }

    public boolean withdrawAccount(UUID accountId, BigDecimal amount) {
        if (!accountImpl.findById(accountId)) {
            return false;
        }

        Account account = accountImpl.getAccountById(accountId);
        BigDecimal newBalance = account.getBalance().subtract(amount);


        if (!account.isOverdraftAllowed() && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Retrait refusé : solde insuffisant !");
            return false;
        }
        if (account.isOverdraftAllowed() &&
                newBalance.compareTo(account.getOverdraftLimit().negate()) < 0) {
            System.out.println("Retrait refusé : limite de découvert atteinte !");
            return false;
        }

        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        return accountImpl.withdraw(accountId, newBalance);
    }

}
