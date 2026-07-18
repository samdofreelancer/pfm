# 📣 Domain Events & Event-Driven Communication

## Overview

Domain events represent things that happened in the domain. They are used to communicate state changes across bounded contexts while preserving loose coupling.

## Event Types

- `TransactionCreated`
- `AccountBalanceChanged`
- `BudgetExceeded`
- `GoalReached`
- `UserRegistered`

## Event Model

Domain events are typically published by aggregates or application services after a state change. Event consumers handle side effects such as notifications, budget updates, and history logging.

## Example: TransactionCreated Event

```java
package com.pfm.domain.transaction.event;

public class TransactionCreatedEvent {
    private final TransactionId transactionId;
    private final UserId userId;
    private final AccountId accountId;
    private final Money amount;
    private final TransactionType type;
    private final LocalDate transactionDate;

    public TransactionCreatedEvent(
        TransactionId transactionId,
        UserId userId,
        AccountId accountId,
        Money amount,
        TransactionType type,
        LocalDate transactionDate
    ) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
    }

    // getters omitted for brevity
}
```

## Event Handler Example

```java
package com.pfm.application.budget.event;

@Service
public class TransactionCreatedEventListener {

    private final BudgetService budgetService;

    @EventListener
    public void handle(TransactionCreatedEvent event) {
        if (event.getType() == TransactionType.EXPENSE) {
            budgetService.updateSpentAmount(event.getUserId(), event.getTransactionId(), event.getAmount());
        }
    }
}
```

## Event-Driven Communication

### Transaction → Budget
- `TransactionCreated` triggers budget updates for expense transactions
- Budget context recalculates spent totals and checks thresholds

### Transaction → Account
- `TransactionCreated` may update account balance through account domain logic

### Budget → Notification
- `BudgetExceeded` triggers notification delivery

### Goal → Notification
- `GoalReached` triggers alert or congratulatory message

## Guidelines

- Keep events simple and descriptive
- Prefer immutable event objects
- Publish events only after the aggregate state is valid and persisted
- Use events to decouple bounded contexts
- Avoid business logic in event handlers; handlers should orchestrate side effects

## Benefits

- Loose coupling across modules
- Better scalability and resilience
- Clear history of domain changes
- Easier integration with asynchronous workflows
