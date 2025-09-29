package org.bank.domain;

public enum Currency {
    EUR("EUR", "Euro", "€"),
    USD("USD", "US Dollar", "$"),
    MAD("MAD", "Dirham Marocain", "DH");

    private final String code;
    private final String name;
    private final String symbol;

    Currency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return code + " (" + name + ")";
    }

    public static Currency fromCode(String code) {
        if (code == null) return MAD;
        
        for (Currency currency : values()) {
            if (currency.code.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        return MAD;
    }
}