package org.bank.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface FeeRuleRepository {
    boolean addFeeRuleParTransaction(UUID accountId,BigDecimal prix);
    boolean update(UUID accountId,BigDecimal value);
    boolean delete(UUID accountId);
}
