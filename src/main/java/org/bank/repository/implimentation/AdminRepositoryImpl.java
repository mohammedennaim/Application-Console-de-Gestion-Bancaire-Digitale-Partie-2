package org.bank.repository.implimentation;

import java.math.BigDecimal;
import java.util.UUID;

import org.bank.domain.Account;
import org.bank.domain.Auditor;
import org.bank.domain.Client;
import org.bank.domain.Credit;
import org.bank.domain.Currency;
import org.bank.domain.Manager;
import org.bank.domain.Teller;
import org.bank.domain.Transaction.TransactionStatus;
import org.bank.domain.Transaction.TransactionType;
import org.bank.repository.AdminRepository;

public class AdminRepositoryImpl implements AdminRepository {

    @Override
    public boolean createAccount(Account account) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAccount'");
    }

    @Override
    public boolean deleteAccount(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
    }

    @Override
    public boolean updateAccount(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAccount'");
    }

    @Override
    public boolean createClient(Client client) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createClient'");
    }

    @Override
    public boolean deleteClient(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteClient'");
    }

    @Override
    public boolean updateClient(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateClient'");
    }

    @Override
    public boolean createAuditor(Auditor auditor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAuditor'");
    }

    @Override
    public boolean deleteAuditor(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuditor'");
    }

    @Override
    public boolean updateAuditor(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAuditor'");
    }

    @Override
    public boolean createTeller(Teller teller) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTeller'");
    }

    @Override
    public boolean deleteTeller(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteTeller'");
    }

    @Override
    public boolean updateTeller(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateTeller'");
    }

    @Override
    public boolean createManager(Manager manager) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createManager'");
    }

    @Override
    public boolean deleteManager(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteManager'");
    }

    @Override
    public boolean updateManager(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateManager'");
    }

    @Override
    public boolean createCredit(Credit credit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCredit'");
    }

    @Override
    public boolean deleteCredit(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCredit'");
    }

    @Override
    public boolean updateCredit(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCredit'");
    }

    @Override
    public boolean transfer(UUID transactionId, TransactionType transactionType, TransactionStatus transactionStatus,
            UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, BigDecimal fee, Currency currency,
            UUID initiatedByUserId, String externalReference, String description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'transfer'");
    }
}
