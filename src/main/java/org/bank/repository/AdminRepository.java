package org.bank.repository;

import org.bank.domain.*;

import java.math.BigDecimal;
import java.util.UUID;

public interface AdminRepository {
    boolean createAccount(Account account);
    boolean deleteAccount(UUID id);
    boolean updateAccount(UUID id);

    boolean createClient(Client client);
    boolean deleteClient(UUID id);
    boolean updateClient(UUID id);

    boolean createAuditor(Auditor auditor);
    boolean deleteAuditor(UUID id);
    boolean updateAuditor(UUID id);

    boolean createTeller(Teller teller);
    boolean deleteTeller(UUID id);
    boolean updateTeller(UUID id);

    boolean createManager(Manager manager);
    boolean deleteManager(UUID id);
    boolean updateManager(UUID id);

    boolean createCredit(Credit credit);
    boolean deleteCredit(UUID id);
    boolean updateCredit(UUID id);

    boolean transfer(UUID transactionId,
                     Transaction.TransactionType transactionType,
                     Transaction.TransactionStatus transactionStatus,
                     UUID sourceAccountId,
                     UUID targetAccountId,
                     BigDecimal amount,
                     BigDecimal fee,
                     Currency currency,
                     UUID initiatedByUserId,
                     String externalReference,
                     String description);

}

