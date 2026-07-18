# 🧩 Domain Model & Aggregates

## Overview

This document describes the core domain models of the Personal Finance Manager (PFM), organized by **Bounded Contexts** and **Aggregates**.

## Domain Model Diagram

The main aggregates and relationships are:

- **User** aggregate root with profile and account membership
- **Account** aggregate root representing financial accounts
- **Transaction** aggregate root representing income, expense, and transfer records
- **Budget** aggregate root representing spending limits
- **Goal** aggregate root representing savings goals
- **Category** shared entity for transaction classification

## Aggregate Roots

| Aggregate | Bounded Context | Description |
|-----------|-----------------|-------------|
| **User** | User | Represents application users and identity information |
| **Account** | Account | Represents financial accounts (cash, bank, e-wallet) |
| **Transaction** | Transaction | Represents financial activities |
| **Budget** | Budget | Represents monthly spending limits |
| **Goal** | Goal | Represents savings goals and progress |
| **Category** | Shared | Represents transaction categories |

## Aggregate Design Principles

### 1. Account Aggregate
- **Root:** `Account`
- **Invariants:**
  - Balance must never be negative
  - Only active accounts may be used in transactions
  - Account name must be non-empty

### 2. Transaction Aggregate
- **Root:** `Transaction`
- **Invariants:**
  - Amount must be greater than zero
  - Transaction date cannot be in the future
  - For expenses, the account balance must be sufficient
  - For transfers, source and destination accounts must differ

### 3. Budget Aggregate
- **Root:** `Budget`
- **Invariants:**
  - Limit amount must be greater than zero
  - Period must be valid (YYYY-MM format)
  - Category must exist in the system

## Key Relationships

- `User (1) -> Account (N)`
- `User (1) -> Budget (N)`
- `User (1) -> Goal (N)`
- `Account (1) -> Transaction (N)`
- `Category (1) -> Transaction (N)`
- `Budget (1) -> Category (1)`

## Example: Account Aggregate

```java
package com.pfm.domain.account.model;

public class Account extends AggregateRoot<AccountId> {
    private final AccountId id;
    private final UserId userId;
    private final AccountName name;
    private Money balance;
    private final Currency currency;
    private AccountStatus status;

    public void deposit(Money amount) {
        if (amount.isNegative()) {
            throw new DomainException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
        registerEvent(new AccountBalanceChangedEvent(this.id, amount));
    }

    public void withdraw(Money amount) {
        if (amount.isNegative()) {
            throw new DomainException("Amount must be positive");
        }
        if (this.balance.isLessThan(amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        registerEvent(new AccountBalanceChangedEvent(this.id, amount.negate()));
    }
}
```

## Guidelines

- Use Value Objects for primitive concepts such as `Money`, `Email`, and `AccountId`
- Encapsulate business logic inside aggregates
- Enforce invariants with domain exceptions
- Publish domain events when aggregate state changes
- Keep aggregates small and focused
- Reference other aggregates by identifier rather than by object reference
