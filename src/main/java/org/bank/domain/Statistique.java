package org.bank.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Statistique {

    private BigDecimal soldeTotalBanque = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private BigDecimal revenusCredits = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private int nombreTransactions;
    private int nombreComptes;
    private int nombreCreditsActifs;

    public BigDecimal getSoldeTotalBanque() {
        return soldeTotalBanque;
    }

    public BigDecimal getRevenusCredits() {
        return revenusCredits;
    }

    public int getNombreTransactions() {
        return nombreTransactions;
    }

    public int getNombreComptes() {
        return nombreComptes;
    }

    public int getNombreCreditsActifs() {
        return nombreCreditsActifs;
    }

    public void setSoldeTotalBanque(BigDecimal soldeTotalBanque) {
        this.soldeTotalBanque = soldeTotalBanque.setScale(2, RoundingMode.HALF_UP);
    }

    public void setRevenusCredits(BigDecimal revenusCredits) {
        this.revenusCredits = revenusCredits.setScale(2, RoundingMode.HALF_UP);
    }

    public void setNombreTransactions(int nombreTransactions) {
        this.nombreTransactions = nombreTransactions;
    }

    public void setNombreComptes(int nombreComptes) {
        this.nombreComptes = nombreComptes;
    }

    public void setNombreCreditsActifs(int nombreCreditsActifs) {
        this.nombreCreditsActifs = nombreCreditsActifs;
    }
}
