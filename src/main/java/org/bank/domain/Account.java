package org.bank.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;

public class Account {
    public enum AccountType {
        COURANT,
        EPARGNE,
        CREDIT
    }
    private String id; // ex: BK-2025-0001
    private long clientId; // id du client (User avec rôle CLIENT)
    private AccountType type;
    private BigDecimal balance;
    private Currency currency;
    private boolean overdraftAllowed;
    private BigDecimal overdraftLimit; // ex: 1000.00 (si overdraftAllowed = true)
    private boolean closed;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    public Account() {
    }

    public Account(String id,
                   long clientId,
                   AccountType type,
                   BigDecimal balance,
                   Currency currency,
                   boolean overdraftAllowed,
                   BigDecimal overdraftLimit,
                   LocalDateTime openedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.clientId = clientId;
        this.type = Objects.requireNonNull(type, "type");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.overdraftAllowed = overdraftAllowed;
        this.overdraftLimit = overdraftLimit == null ? BigDecimal.ZERO : overdraftLimit;
        this.openedAt = openedAt == null ? LocalDateTime.now() : openedAt;
        this.closed = false;
        setBalance(balance == null ? BigDecimal.ZERO : balance);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOwnerId() {
        return clientId;
    }

    public void setOwnerId(long ownerId) {
        this.clientId = ownerId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        if (balance == null) balance = BigDecimal.ZERO;
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isOverdraftAllowed() {
        return overdraftAllowed;
    }

    public void setOverdraftAllowed(boolean overdraftAllowed) {
        this.overdraftAllowed = overdraftAllowed;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit == null ? BigDecimal.ZERO : overdraftLimit.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isClosed() {
        return closed;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", clientId=" + clientId +
                ", type=" + type +
                ", balance=" + balance +
                ", currency=" + currency +
                ", overdraftAllowed=" + overdraftAllowed +
                ", overdraftLimit=" + overdraftLimit +
                ", closed=" + closed +
                ", openedAt=" + openedAt +
                ", closedAt=" + closedAt +
                '}';
    }
}