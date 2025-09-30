package org.bank.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    public enum TransactionStatus {
        PENDING, SETTLED, FAILED, REVERSED
    }
    public enum TransactionType {
        TRANSFER,
        TRANSFER_EXTERNAL
    }

    private UUID id;
    private TransactionType type;
    private TransactionStatus status;

    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private BigDecimal fee;
    private Currency currency;

    private UUID initiatedByUserId;
    private String externalReference;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime executedAt;


    public Transaction(UUID id,
                       TransactionType type,
                       TransactionStatus status,
                       UUID sourceAccountId,
                       UUID targetAccountId,
                       BigDecimal amount,
                       BigDecimal fee,
                       Currency currency,
                       UUID initiatedByUserId,
                       String externalReference,
                       String description,
                       LocalDateTime createdAt,
                       LocalDateTime executedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.status = status == null ? TransactionStatus.PENDING : status;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : amount.setScale(2, RoundingMode.HALF_UP);
        this.fee = fee == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : fee.setScale(2, RoundingMode.HALF_UP);
        this.currency = Objects.requireNonNull(currency, "currency");
        this.initiatedByUserId = initiatedByUserId;
        this.externalReference = externalReference;
        this.description = description;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.executedAt = executedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(UUID targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public UUID getInitiatedByUserId() {
        return initiatedByUserId;
    }

    public void setInitiatedByUserId(UUID initiatedByUserId) {
        this.initiatedByUserId = initiatedByUserId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}