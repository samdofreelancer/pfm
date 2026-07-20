package com.pfm.domain.account.model;

import com.pfm.domain.shared.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Account {
    private final AccountId id;
    private final AccountOwnerId userId;
    private AccountType type;
    private String name;
    private String description;
    private BigDecimal balance;
    private String currency;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Account(AccountId id, AccountOwnerId userId, AccountType type, String name, String description,
                    BigDecimal balance, String currency) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.name = validateName(name);
        this.description = description;
        this.balance = validateBalance(balance);
        this.currency = validateCurrency(currency);
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Account create(AccountOwnerId userId, AccountType type, String name, String description,
                                 BigDecimal balance, String currency) {
        AccountId accountId = AccountId.generate();
        Account account = new Account(accountId, userId, type, name, description, balance, currency);
        return account;
    }

    public static Account restore(AccountId id, AccountOwnerId userId, AccountType type, String name, String description,
                                  BigDecimal balance, String currency, boolean isActive,
                                  LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        Account account = new Account(id, userId, type, name, description, balance, currency);
        account.isActive = isActive;
        account.createdAt = createdAt;
        account.updatedAt = updatedAt;
        account.deletedAt = deletedAt;
        return account;
    }

    public void update(String name, String description, AccountType type) {
        this.name = validateName(name);
        this.description = description;
        if (type == null) {
            throw new IllegalArgumentException("Account type must not be null");
        }
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal newBalance) {
        this.balance = validateBalance(newBalance);
        this.updatedAt = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name must not be blank");
        }
        return name.trim();
    }

    private static BigDecimal validateBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Account balance must not be null");
        }
        return balance;
    }

    private static String validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
        return currency.trim().toUpperCase();
    }
}
