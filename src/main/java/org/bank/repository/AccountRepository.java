package org.bank.repository;

import org.bank.domain.Account;
import java.util.UUID;

public interface AccountRepository {
    boolean save(Account account);
    Account findById(UUID id);
}

