# 💰 Value Objects

## Overview

Value objects are immutable objects that represent a concept with no identity. PFM uses value objects to encapsulate domain rules and prevent primitive obsession.

## Common Value Objects

| Value Object | Description | Validation Rules |
|--------------|-------------|------------------|
| **Money** | Amount with currency | Amount must be >= 0 |
| **Email** | User email address | Must be valid email format |
| **UserId** | User identifier | UUID format |
| **AccountId** | Account identifier | UUID format |
| **AccountName** | Account display name | Non-empty, max length 100 |
| **Percentage** | Percent value | 0-100 |
| **DateRange** | Start and end date | Start <= End |
| **BudgetPeriod** | Monthly period | Valid year-month |

## Example: Money Value Object

```java
package com.pfm.domain.shared.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        validateAmount(amount);
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new DomainException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Amount cannot be negative");
        }
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new DomainException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new DomainException("Cannot subtract different currencies");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
```

## Example: Email Value Object

```java
package com.pfm.domain.user.model;

import java.util.Objects;

public final class Email {
    private final String value;

    public Email(String value) {
        validateEmail(value);
        this.value = value.toLowerCase();
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainException("Email cannot be empty");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new DomainException("Invalid email format");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

## Example: Percentage Value Object

```java
package com.pfm.domain.shared.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Percentage {
    private final double value;
    private static final double MAX = 100.0;
    private static final double MIN = 0.0;

    public Percentage(double value) {
        validatePercentage(value);
        this.value = value;
    }

    private void validatePercentage(double value) {
        if (value < MIN || value > MAX) {
            throw new DomainException(
                String.format("Percentage must be between %.1f and %.1f", MIN, MAX)
            );
        }
    }

    public static Percentage of(Money spent, Money limit) {
        if (limit.isZero()) {
            return new Percentage(0);
        }
        double percent = spent.getAmount()
            .divide(limit.getAmount(), 2, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
        return new Percentage(Math.min(percent, MAX));
    }

    public boolean isExceeded() {
        return value >= 100.0;
    }

    public boolean isWarning() {
        return value >= 80.0 && value < 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Percentage that = (Percentage) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

## Guidelines for Value Objects

- Always immutable – use `final` fields and no setters
- Self-validating – validate inside the constructor
- No identity – equality is based on all fields
- Encapsulate business methods such as `add()` and `subtract()`
- Override `equals()` and `hashCode()` for collection use
- Use factory methods such as `of()` or `from()` when appropriate
- No side effects – operations return new instances

## When to Use Value Objects

✅ Use value objects for:
- Amounts with currency (`Money`)
- Identifiers (`UserId`, `AccountId`)
- Email addresses
- Dates and ranges
- Percentages
- Names and descriptions
- Statuses and types

❌ Avoid value objects for:
- Entities with mutable state
- Objects that require database identity
- Objects with complex lifecycles
- Objects with external dependencies
