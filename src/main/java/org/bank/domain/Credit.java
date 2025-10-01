package org.bank.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


public class Credit {

    public enum CreditStatus {
        PENDING, APPROVED, REJECTED, ACTIVE, COMPLETED, DEFAULTED
    }

    public enum InterestMode {
        SIMPLE, COMPOUND // SIMPLE: intérêts linéaires, COMPOUND: amortissement (taux mensuel)
    }

    public static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

    private String id; // ex: CR-2025-0001
    private UUID clientId;
    private String linkedAccountId; // compte débité/crédité automatiquement
    private BigDecimal principal; // capital
    private BigDecimal annualRate; // 8% => 0.08
    private int durationMonths; // durée en mois
    private InterestMode interestMode;
    private CreditStatus status;
    private BigDecimal penaltyRate; // pénalité mensuelle sur échéance en retard (ex: 0.01 = 1%)
    private LocalDate startDate; // date de départ de l'échéancier
//    private final List<T> schedule; // échéancier

    public Credit() {
//        this.schedule = new ArrayList<>();
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
//        this.schedule = new ArrayList<>();
        this.status = CreditStatus.APPROVED; // typiquement après validation manager
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

//    public List<Installment> getSchedule() {
//        return schedule;
//    }

    public void setStatus(CreditStatus status) {
        this.status = status;
    }
}