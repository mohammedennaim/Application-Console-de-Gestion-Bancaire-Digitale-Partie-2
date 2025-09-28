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
    public void createClient(Client client){
        if (!clientImpl.findById(UUID.fromString(client.getId().toString()))){
            clientImpl.save(client);
            System.out.println("Creation de client is success");
            return;
        }

        System.out.println("cette client il deja exist");
    }

    public boolean createAccount(Account account){
        return accountService.createAccount(account);
    }



}
