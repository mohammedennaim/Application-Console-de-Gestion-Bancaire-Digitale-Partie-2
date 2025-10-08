package org.bank.service;
import org.bank.repository.implimentation.FeeRuleRepositoryImpl;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class FeeRuleService {
    FeeRuleRepositoryImpl fee = new FeeRuleRepositoryImpl();

    public FeeRuleService() throws SQLException {
    }
    public void addFeeRuleParTransaction(UUID accountID, BigDecimal amount){
        fee.addFeeRuleParTransaction(accountID, amount);
    }
}
