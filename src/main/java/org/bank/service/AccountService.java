package org.bank.service;

import org.bank.domain.Account;
import org.bank.repository.implimentation.AccountRepositoryImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class AccountService {
    AccountRepositoryImpl accountImpl = new AccountRepositoryImpl();

    public AccountService() throws SQLException {
    }
    
    // Getter pour accéder au repository depuis d'autres services
    public AccountRepositoryImpl getAccountRepository() {
        return accountImpl;
    }

    public boolean findAccount(Account account){
        return accountImpl.findById(UUID.fromString(account.getId().toString()));
    }

    public Account getAccountById(String accountId){
        if (accountImpl.findById(UUID.fromString(accountId))){
            return accountImpl.getAccountById(UUID.fromString(accountId));
        }
        System.out.println("cette account il n'exist pas");
        return null;
    }

    public boolean getAccountByClientId(UUID clientId){
        return accountImpl.getAccountByClientId(clientId) != null;
    }

    public Account getCreditAccountByClientId(UUID clientId) {
        return accountImpl.getCreditAccountByClientId(clientId);
    }

    public boolean hasCreditAccount(UUID clientId) {
        Account creditAccount = accountImpl.getCreditAccountByClientId(clientId);
        return creditAccount != null;
    }

    public void createAccount(Account account){

        if (accountImpl.findById(UUID.fromString(account.getId().toString()))){
            return;
        }
        if (account.getType() == Account.AccountType.CREDIT){
            accountImpl.createAccountCredit(account);
            return;
        }
        accountImpl.save(account);
    }

    public boolean deleteAccount(Account account){
        if (accountImpl.findById(UUID.fromString(account.getId().toString()))){
            accountImpl.delete(UUID.fromString(account.getId().toString()));
            return true;
        }
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
            System.err.println("Erreur lors du withdraw");
            return false;
        }
    }

}
