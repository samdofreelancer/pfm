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
    private final AccountId userId;
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

    private Account(AccountId id, AccountId userId, AccountType type, String name, String description,
                    BigDecimal balance, String currency) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.currency = currency;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Account create(AccountId userId, AccountType type, String name, String description,
                                 BigDecimal balance, String currency) {
        AccountId accountId = AccountId.generate();
        Account account = new Account(accountId, userId, type, name, description, balance, currency);
        return account;
    }

    public static Account restore(AccountId id, AccountId userId, AccountType type, String name, String description,
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
        this.name = name;
        this.description = description;
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
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
}