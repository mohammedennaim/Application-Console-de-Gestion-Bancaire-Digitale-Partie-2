package org.bank.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public class Account {
    public enum AccountType {
        COURANT,
        EPARGNE,
        CREDIT
    }
    private UUID id;
    private UUID clientId;
    private String bankCode;
    private AccountType type;
    private BigDecimal balance;
    private Currency currency;
    private boolean overdraftAllowed;
    private BigDecimal overdraftLimit;
    private boolean closed;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private LocalDateTime updatedAt;

    public Account() {
    }

    public Account(UUID id,
                   UUID clientId,
                   String bankCode,
                   AccountType type,
                   BigDecimal balance,
                   Currency currency,
                   boolean overdraftAllowed,
                   BigDecimal overdraftLimit,
                   LocalDateTime openedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.clientId = clientId;
        this.bankCode = bankCode;
        this.type = Objects.requireNonNull(type, "type");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.overdraftAllowed = overdraftAllowed;
        this.overdraftLimit = overdraftLimit == null ? BigDecimal.ZERO : overdraftLimit;
        this.openedAt = openedAt == null ? LocalDateTime.now() : openedAt;
        this.closed = false;
        this.updatedAt = LocalDateTime.now();
        setBalance(balance == null ? BigDecimal.ZERO : balance);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return clientId;
    }

    public void setOwnerId(UUID ownerId) { this.clientId = ownerId; }

    public String getBankCode() { return bankCode; }

    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

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

    public void setClosed(boolean closed) {
        this.closed = closed;
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

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", clientId=" + clientId +
                ",bankCode=" + bankCode +
                ", type=" + type +
                ", balance=" + balance +
                ", currency=" + currency +
                ", overdraftAllowed=" + overdraftAllowed +
                ", overdraftLimit=" + overdraftLimit +
                ", closed=" + closed +
                ", openedAt=" + openedAt +
                ", closedAt=" + closedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}