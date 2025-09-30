package org.bank.repository;

import org.bank.domain.Account;
import org.bank.domain.Client;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountRepository {
    boolean save(Account account);
    boolean findById(UUID id);
    boolean delete(UUID id);
    boolean deposit(UUID id, BigDecimal balance);
    boolean withdraw(UUID id, BigDecimal balance);
    
    // Nouvelles méthodes pour la validation des transferts
    Account getAccountById(UUID accountId);
    UUID getClientIdByAccountId(UUID accountId);
}

