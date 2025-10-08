package org.bank.repository;

import org.bank.domain.Account;
import org.bank.domain.Client;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountRepository {
    boolean save(Account account);
    boolean findById(UUID id);
    boolean deposit(UUID id, BigDecimal balance);
    boolean withdraw(UUID id, BigDecimal balance);
    Account getAccountById(UUID accountId);
    Client getClientByAccountId(UUID accountId);
}

