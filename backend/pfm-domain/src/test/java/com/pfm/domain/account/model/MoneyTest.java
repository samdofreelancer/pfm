package com.pfm.domain.account.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void of_ShouldNormalizeCurrency_WhenCurrencyIsLowercase() {
        Money money = Money.of(new BigDecimal("10.50"), "vnd");

        assertEquals(new BigDecimal("10.50"), money.getAmount());
        assertEquals("VND", money.getCurrency());
    }

    @Test
    void of_ShouldRejectBlankCurrency() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.ONE, " "));
    }

    @Test
    void of_ShouldRejectInvalidCurrencyCode() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.ONE, "VN"));
    }

    @Test
    void add_ShouldReturnSum_WhenCurrenciesMatch() {
        Money result = Money.of(new BigDecimal("10.00"), "VND")
                .add(Money.of(new BigDecimal("2.50"), "VND"));

        assertEquals(new BigDecimal("12.50"), result.getAmount());
        assertEquals("VND", result.getCurrency());
    }

    @Test
    void subtract_ShouldReturnDifference_WhenCurrenciesMatch() {
        Money result = Money.of(new BigDecimal("10.00"), "VND")
                .subtract(Money.of(new BigDecimal("2.50"), "VND"));

        assertEquals(new BigDecimal("7.50"), result.getAmount());
    }

    @Test
    void add_ShouldRejectDifferentCurrencies() {
        Money vnd = Money.of(BigDecimal.ONE, "VND");
        Money usd = Money.of(BigDecimal.ONE, "USD");

        assertThrows(IllegalArgumentException.class, () -> vnd.add(usd));
    }

    @Test
    void isLessThan_ShouldCompareAmounts_WhenCurrenciesMatch() {
        assertTrue(Money.of(BigDecimal.ONE, "VND").isLessThan(Money.of(BigDecimal.TEN, "VND")));
    }

    @Test
    void zero_ShouldCreateZeroMoneyForCurrency() {
        Money money = Money.zero("usd");

        assertEquals(BigDecimal.ZERO, money.getAmount());
        assertEquals("USD", money.getCurrency());
    }
}
