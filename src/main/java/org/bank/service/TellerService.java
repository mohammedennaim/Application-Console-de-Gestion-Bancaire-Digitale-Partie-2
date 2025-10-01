package org.bank.service;

import org.bank.domain.Account;
import org.bank.domain.Client;
import org.bank.domain.Currency;
import org.bank.domain.Transaction;
import org.bank.repository.implimentation.TellerRepositoryImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class TellerService {
    TellerRepositoryImpl teller = new TellerRepositoryImpl();
    AccountService accountService = new AccountService();
    TransactionService transactionService = new TransactionService();
    public TellerService() throws SQLException {

    }
    public boolean createClient(Client client){
        if (!teller.findClientParTeller(client)){
            teller.createClient(client);
//            System.out.println("Creation de client is success");
            return true;
        }

//        System.out.println("cette client il deja exist");
        return false;
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

    public boolean transferParTeller(UUID transactionId,
                             Transaction.TransactionType transactionType,
                             Transaction.TransactionStatus transactionStatus,
                             UUID sourceAccountId,
                             UUID targetAccountId,
                             BigDecimal amount,
                             Currency currency,
                             UUID initiatedByUserId,
                             String externalReference,
                             String description){
        return  transactionService.transfer(transactionId,transactionType,transactionStatus,sourceAccountId,
                targetAccountId,amount,BigDecimal.ZERO,currency,initiatedByUserId,externalReference,description);

    }
    public boolean transferExterneParTeller(UUID transactionId,
                                     Transaction.TransactionType transactionType,
                                     Transaction.TransactionStatus transactionStatus,
                                     UUID sourceAccountId,
                                     UUID targetAccountId,
                                     BigDecimal amount,
                                     Currency currency,
                                     UUID initiatedByUserId,
                                     String externalReference,
                                     String description){
        return  transactionService.transferExterne(transactionId,transactionType,transactionStatus,sourceAccountId,
                targetAccountId,amount, BigDecimal.valueOf(15.00),currency,initiatedByUserId,externalReference,description);
    }
}
