package org.bank.service;

import org.bank.domain.Account;
import org.bank.domain.Client;
import org.bank.repository.implimentation.ClientRepositoryImpl;

import java.sql.SQLException;
import java.util.UUID;

public class TellerService {
    ClientRepositoryImpl clientImpl = new ClientRepositoryImpl();
    AccountService accountService = new AccountService();
    public TellerService() throws SQLException {

    }
    public boolean createClient(Client client){
        if (!clientImpl.findById(UUID.fromString(client.getId().toString()))){
            System.out.println("cette client il deja exist");
            return false;
        }
        clientImpl.save(client);
        System.out.println("Creation de client is success");
        return true;
    }

    public boolean createAccount(Account account){
        return accountService.createAccount(account);
    }



}
