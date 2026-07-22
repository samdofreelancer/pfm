package com.pfm.domain.account.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Money {
    BigDecimal amount;
    String currency;

    private Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount must not be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
        String normalizedCurrency = currency.trim().toUpperCase();
        if (!normalizedCurrency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code");
        }
        this.amount = amount;
        this.currency = normalizedCurrency;
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return amount.compareTo(other.amount) < 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private void assertSameCurrency(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Money must not be null");
        }
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }
}
