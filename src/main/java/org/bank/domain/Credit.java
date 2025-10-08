package org.bank.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


public class Credit {

    public enum CreditStatus {
        PENDING, APPROVED, REJECTED, ACTIVE, COMPLETED, DEFAULTED
    }

    public enum InterestMode {
        SIMPLE, COMPOUND
    }

    private String id;
    private UUID clientId;
    private String linkedAccountId;
    private BigDecimal principal;
    private BigDecimal annualRate;
    private int durationMonths;
    private InterestMode interestMode;
    private CreditStatus status;
    private BigDecimal penaltyRate;
    private LocalDate startDate;

    public Credit() {
        this.status = CreditStatus.PENDING;
    }

    public Credit(String id,
                  UUID clientId,
                  String linkedAccountId,
                  BigDecimal principal,
                  BigDecimal annualRate,
                  int durationMonths,
                  InterestMode interestMode,
                  BigDecimal penaltyRate,
                  LocalDate startDate) {
        this.id = Objects.requireNonNull(id, "id");
        this.clientId = clientId;
        this.linkedAccountId = linkedAccountId;
        this.principal = principal;
        this.annualRate = annualRate;
        this.durationMonths = durationMonths;
        this.interestMode = interestMode == null ? InterestMode.COMPOUND : interestMode;
        this.penaltyRate = penaltyRate == null ? BigDecimal.ZERO : penaltyRate;
        this.startDate = startDate == null ? LocalDate.now() : startDate;
        this.status = CreditStatus.APPROVED;
    }


    public String getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getLinkedAccountId() {
        return linkedAccountId;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public InterestMode getInterestMode() {
        return interestMode;
    }

    public CreditStatus getStatus() {
        return status;
    }

    public BigDecimal getPenaltyRate() {
        return penaltyRate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }


    public void setStatus(CreditStatus status) {
        this.status = status;
    }
}