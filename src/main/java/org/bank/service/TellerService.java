package org.bank.service;

import org.bank.domain.Account;
import org.bank.domain.Client;
import org.bank.domain.Currency;
import org.bank.domain.Transaction;
import org.bank.repository.implimentation.ClientRepositoryImpl;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class TellerService {
    ClientRepositoryImpl client = new ClientRepositoryImpl();
    AccountService accountService = new AccountService();
    TransactionService transactionService = new TransactionService();
    public TellerService() throws SQLException {

    }
    public boolean createClient(Client client){
        if (!this.client.findById(client.getId())){
            this.client.save(client);
//            System.out.println("Creation de client is success");
            return true;
        }

//        System.out.println("cette client il deja exist");
        return false;
    }

    public boolean updateClient(Client client){
        if (this.client.findById(client.getId())){
            this.client.update(client);
        }
        return true;
    }

    public boolean deleteClient(Client client){
        if (this.client.findById(client.getId())){
            this.client.delete(client.getId());
        }
        return true;
    }

    public boolean depositParTeller(UUID accountId, BigDecimal amount){
        return accountService.depositAccount(accountId,amount);
    }

    public boolean withdrawParTeller(UUID accountId, BigDecimal amount){
        return accountService.withdrawAccount(accountId,amount);
    }

    public boolean createAccount(Account account){
        if (!accountService.findAccount(account)){
            accountService.createAccount(account);
//            System.out.println("Creation de account is success");
            return true;
        }
//        System.out.println("cette account il deja exist");
        return false;
    }

    public boolean transferByTeller(
        UUID transactionId,
        Transaction.TransactionType transactionType,
        Transaction.TransactionStatus transactionStatus,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        Currency currency,
        UUID initiatedByUserId,
        String externalReference,
        String description
        ){
        return  transactionService.transfer(
            transactionId,
            transactionType,
            transactionStatus,
            sourceAccountId,
            targetAccountId,
            amount,
            BigDecimal.ZERO,
            currency,
            initiatedByUserId,
            externalReference,
            description
        );
    }
    public boolean transferExterneByTeller(
        UUID transactionId,
        Transaction.TransactionType transactionType,
        Transaction.TransactionStatus transactionStatus,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        Currency currency,
        UUID initiatedByUserId,
        String externalReference,
        String description
        ){
        return  transactionService.transferExterne(
            transactionId,
            transactionType,
            transactionStatus,
            sourceAccountId,
            targetAccountId,
            amount,
            BigDecimal.valueOf(15.00),
            currency,
            initiatedByUserId,
            externalReference,
            description
        );
    }
    
    /**
     * Récupère l'ID du client propriétaire d'un compte
     */
    public UUID getClientIdByAccountId(UUID accountId) {
        try {
            Client client = accountService.getAccountRepository().getClientByAccountId(accountId);
            return client != null ? client.getId() : null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du client par compte ID: " + e.getMessage());
            return null;
        }
    }
}
