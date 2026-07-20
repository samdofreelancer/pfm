package com.pfm.domain.account.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void create_ShouldInitializeActiveAccountWithTrimmedName() {
        Account account = Account.create(
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "  Wallet  ",
                "Daily cash",
                Money.of(new BigDecimal("100.00"), "vnd")
        );

        assertNotNull(account.getId());
        assertEquals("user-1", account.getUserId().getValue());
        assertEquals("Wallet", account.getName());
        assertEquals(new BigDecimal("100.00"), account.getBalance().getAmount());
        assertEquals("VND", account.getCurrency());
        assertTrue(account.isActive());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    void create_ShouldRejectNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> Account.create(
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Wallet",
                null,
                Money.of(new BigDecimal("-1.00"), "VND")
        ));
    }

    @Test
    void update_ShouldChangeEditableFields() {
        Account account = newAccount();

        account.update("Bank", "Main bank account", AccountType.BANK_ACCOUNT);

        assertEquals("Bank", account.getName());
        assertEquals("Main bank account", account.getDescription());
        assertEquals(AccountType.BANK_ACCOUNT, account.getType());
    }

    @Test
    void deposit_ShouldIncreaseBalance_WhenAmountIsPositiveSameCurrency() {
        Account account = newAccount();

        account.deposit(Money.of(new BigDecimal("25.00"), "VND"));

        assertEquals(new BigDecimal("125.00"), account.getBalance().getAmount());
    }

    @Test
    void deposit_ShouldRejectCurrencyMismatch() {
        Account account = newAccount();

        assertThrows(IllegalArgumentException.class, () -> account.deposit(Money.of(BigDecimal.ONE, "USD")));
    }

    @Test
    void withdraw_ShouldDecreaseBalance_WhenSufficientFunds() {
        Account account = newAccount();

        account.withdraw(Money.of(new BigDecimal("40.00"), "VND"));

        assertEquals(new BigDecimal("60.00"), account.getBalance().getAmount());
    }

    @Test
    void withdraw_ShouldRejectInsufficientFunds() {
        Account account = newAccount();

        assertThrows(IllegalArgumentException.class, () -> account.withdraw(Money.of(new BigDecimal("101.00"), "VND")));
    }

    @Test
    void deactivateAndActivate_ShouldToggleLifecycleState() {
        Account account = newAccount();

        account.deactivate();

        assertFalse(account.isActive());
        assertNotNull(account.getDeletedAt());

        account.activate();

        assertTrue(account.isActive());
        assertNull(account.getDeletedAt());
    }

    private Account newAccount() {
        return Account.create(
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Wallet",
                null,
                Money.of(new BigDecimal("100.00"), "VND")
        );
    }
}
