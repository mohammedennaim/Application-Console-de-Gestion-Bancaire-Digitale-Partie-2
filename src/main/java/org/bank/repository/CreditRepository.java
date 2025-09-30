package org.bank.repository;

import org.bank.domain.Credit;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreditRepository {
    boolean credit(BigDecimal amount, UUID clientID, UUID accountID, BigDecimal fee, Credit.InterestMode type);
}
