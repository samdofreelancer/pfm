package com.pfm.domain.account.model;

import com.pfm.domain.shared.event.DomainEvent;
import lombok.Getter;

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
    private Money balance;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Account(AccountId id, AccountOwnerId userId, AccountType type, String name, String description,
                    Money balance) {
        this.id = id;
        this.userId = userId;
        this.type = validateType(type);
        this.name = validateName(name);
        this.description = description;
        this.balance = validateBalance(balance);
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Account create(AccountOwnerId userId, AccountType type, String name, String description,
                                 Money balance) {
        AccountId accountId = AccountId.generate();
        return new Account(accountId, userId, type, name, description, balance);
    }

    public static Account restore(AccountId id, AccountOwnerId userId, AccountType type, String name, String description,
                                  Money balance, boolean isActive,
                                  LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        Account account = new Account(id, userId, type, name, description, balance);
        account.isActive = isActive;
        account.createdAt = createdAt;
        account.updatedAt = updatedAt;
        account.deletedAt = deletedAt;
        return account;
    }

    public void update(String name, String description, AccountType type) {
        this.name = validateName(name);
        this.description = description;
        this.type = validateType(type);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBalance(Money newBalance) {
        this.balance = validateBalance(newBalance);
        this.updatedAt = LocalDateTime.now();
    }

    public void deposit(Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (this.balance.isLessThan(amount)) {
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

    public String getCurrency() {
        return balance.getCurrency();
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name must not be blank");
        }
        return name.trim();
    }

    private static AccountType validateType(AccountType type) {
        if (type == null) {
            throw new IllegalArgumentException("Account type must not be null");
        }
        return type;
    }

    private static Money validateBalance(Money balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Account balance must not be null");
        }
        if (balance.isNegative()) {
            throw new IllegalArgumentException("Account balance must not be negative");
        }
        return balance;
    }
}
