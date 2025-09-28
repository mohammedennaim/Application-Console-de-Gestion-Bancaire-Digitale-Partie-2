package org.bank.service;

import org.bank.domain.Account;
import org.bank.repository.implimentation.AccountRepositoryImpl;

import java.sql.SQLException;
import java.util.UUID;

public class AccountService {
    AccountRepositoryImpl accountImpl = new AccountRepositoryImpl();

    public AccountService() throws SQLException {
    }

    public boolean createAccount(Account account){
        if (!accountImpl.findById(UUID.fromString(account.getId().toString()))){
            System.out.println("cette account il deja exist");
            return false;
        }
        accountImpl.save(account);
        System.out.println("Creation de account is success");
        return true;
    }
}
