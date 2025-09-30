package org.bank.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;

public class FeeRule {
    public enum Mode { FIX, PERCENT }
    public enum OperationType { TRANSFER_EXTERNAL, WITHDRAW_FOREIGN_CURRENCY }

    private long id;
    private OperationType operationType;
    private Mode mode;
    private BigDecimal value;
    private Currency currency;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FeeRule(long id,
                   OperationType operationType,
                   Mode mode,
                   BigDecimal value,
                   Currency currency,
                   boolean active) {
        this.id = id;
        this.operationType = Objects.requireNonNull(operationType);
        this.mode = Objects.requireNonNull(mode, "mode");
        this.value = Objects.requireNonNull(value, "value");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.active = active;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public long getId() {
        return id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
        this.updatedAt = LocalDateTime.now();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
        this.updatedAt = LocalDateTime.now();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
