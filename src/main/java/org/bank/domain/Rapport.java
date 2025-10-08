package org.bank.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Rapport {
    private String title;
    private String period;
    private LocalDateTime generatedAt;
    private Statistique stats;
    private List<String> lines;

    public Rapport() {
        this.lines = new ArrayList<>();
        this.generatedAt = LocalDateTime.now();
    }

    public Rapport(String title, String period, Statistique stats) {
        this();
        this.title = title;
        this.period = period;
        this.stats = stats;
    }

    public String getTitle() {
        return title;
    }

    public String getPeriod() {
        return period;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public Statistique getStats() {
        return stats;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setStats(Statistique stats) {
        this.stats = stats;
    }
}