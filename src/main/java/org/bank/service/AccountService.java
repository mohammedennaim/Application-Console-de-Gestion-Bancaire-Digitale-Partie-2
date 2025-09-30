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
            System.err.println("Erreur: Le compte avec l'ID " + accountId + " n'existe pas");
            return false;
        }
        if (accountImpl.getAccountById(accountId).getType().equals(Account.AccountType.CREDIT)){
            System.err.println("Erreur: Impossible de déposer sur un compte de type CREDIT");
            return false;
        }

        boolean result = accountImpl.deposit(accountId, amount);
        if (result) {
            System.out.println("Dépôt de " + amount + " effectué avec succès sur le compte " + accountId);
            System.out.println("Nouveau solde: " + accountImpl.getAccountById(accountId).getBalance());
        } else {
            System.err.println("Erreur lors du dépôt");
        }
        return result;
    }

    public boolean withdrawAccount(UUID accountId, BigDecimal amount) {
        if (!accountImpl.findById(accountId)) {
            System.err.println("Erreur: Le compte avec l'ID " + accountId + " n'existe pas");
            return false;
        }
        if (!accountImpl.getAccountById(accountId).getType().equals(Account.AccountType.COURANT)){
            System.err.println("Erreur: Impossible de withdraw sur un compte de type Credit ou Epargne");
            return false;
        }

        boolean result=  accountImpl.withdraw(accountId, amount);
        if (result) {
            System.out.println("Withdraw de " + amount + " effectué avec succès sur le compte " + accountId);
            System.out.println("Nouveau solde: " + accountImpl.getAccountById(accountId).getBalance());
            return true;
        } else {
            System.err.println("Erreur lors du dépôt");
            return false;
        }
    }

}
