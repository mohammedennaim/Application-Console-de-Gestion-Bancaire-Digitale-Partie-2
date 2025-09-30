package org.bank;

import org.bank.service.AccountService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        AccountService accountService = new AccountService();
         accountService.withdrawAccount(UUID.fromString("1a23acda-3145-48b0-8440-b3126a95834c"), BigDecimal.valueOf(150.00));
                                                            
    }
}