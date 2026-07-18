# ⚙️ Domain Services

## Overview

Domain services encapsulate business logic that does not naturally belong to a single aggregate. They coordinate multiple aggregates or complex domain rules.

## When to Use Domain Services

Use a domain service when:
- The operation involves multiple aggregates
- The logic is not naturally part of one aggregate
- The operation represents a domain concept rather than an application task

## Example: Account Transfer Service

```java
package com.pfm.domain.account.service;

public class AccountTransferService {

    private final AccountRepository accountRepository;

    public AccountTransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void transfer(AccountId fromAccountId, AccountId toAccountId, Money amount) {
        Account fromAccount = accountRepository.findById(fromAccountId);
        Account toAccount = accountRepository.findById(toAccountId);

        if (fromAccountId.equals(toAccountId)) {
            throw new DomainException("Source and destination accounts must differ");
        }

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
```

## Example: Budget Calculation Service

```java
package com.pfm.domain.budget.service;

public class BudgetCalculator {

    public BudgetStatus calculateStatus(Budget budget, Money spent) {
        Percentage usage = Percentage.of(spent, budget.getLimit());

        if (usage.isExceeded()) {
            return BudgetStatus.EXCEEDED;
        }
        if (usage.isWarning()) {
            return BudgetStatus.NEAR_LIMIT;
        }
        return BudgetStatus.ON_TRACK;
    }
}
```

## Guidelines for Domain Services

- Keep domain services stateless whenever possible
- Use them for domain logic that spans multiple aggregates
- Avoid placing application orchestration in domain services
- Prefer meaningful domain service names (e.g. `BudgetCalculator`, `TransactionValidator`)
- Keep domain services inside the domain layer

## Example Use Cases

- Calculating budget utilization
- Validating transaction business rules across account and budget aggregates
- Converting external currency values into domain `Money`
- Resolving category assignments for a transaction

## Best Practices

- Domain services should depend only on domain interfaces and value objects
- Use domain exceptions to handle invalid domain conditions
- Do not inject infrastructure-specific classes directly into domain services
- Keep service methods expressive and easy to test
