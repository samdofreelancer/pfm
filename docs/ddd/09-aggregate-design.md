# 🧱 Aggregate Design Principles

## Overview

Aggregates are consistency boundaries in DDD. They group related entities and value objects around a single root that enforces invariants.

## Aggregate Definition

An aggregate is a cluster of domain objects that are treated as a single unit for data changes. The aggregate root is the only object that external code may hold a reference to.

## Key Principles

- Each aggregate has one root entity
- External interactions must go through the root
- Aggregate invariants are enforced by the root
- Aggregate boundaries should be small and consistent
- Use IDs to reference other aggregates, not object references

## Aggregate Design Guidelines

### 1. Keep Aggregates Small

Large aggregates increase transaction size and complexity. Keep aggregate boundaries focused on a single transactional consistency requirement.

### 2. Enforce Invariants at the Root

Only the aggregate root should ensure invariants such as balance constraints or status rules.

### 3. Reference by ID

Use identifiers to link aggregates across contexts. For example, `Transaction` may hold `accountId` and `categoryId`, not a full `Account` object.

### 4. Avoid Cross-Aggregate Transactions

Transactions should normally cover a single aggregate. For multi-aggregate changes, use domain services or eventual consistency with events.

### 5. Strong Consistency Within Aggregates

Everything within an aggregate should be consistent at the end of a transaction.

## Aggregate Examples

### Account Aggregate

- Root: `Account`
- Contains `AccountName`, `Money balance`, `AccountStatus`
- Business methods: `deposit()`, `withdraw()`, `activate()`, `close()`

### Transaction Aggregate

- Root: `Transaction`
- Contains `TransactionType`, `Money amount`, `CategoryId`, `TransactionDate`
- Business methods: `validate()`, `complete()`, `cancel()`

### Budget Aggregate

- Root: `Budget`
- Contains `Money limit`, `BudgetPeriod`, `CategoryId`, `Money spent`
- Business methods: `addExpense()`, `isExceeded()`, `updateLimit()`

## Aggregate Boundaries

### When to Choose a Separate Aggregate

- If the object has its own lifecycle
- If it must be modified independently
- If it requires separate transaction boundaries

### When to Include as Part of an Aggregate

- If it is only meaningful within the parent aggregate
- If it cannot exist without the root
- If its state changes always follow the root

## Common Design Patterns

### Aggregate Root as Transaction Boundary

```java
public class Account extends AggregateRoot<AccountId> {
    private Money balance;

    public void withdraw(Money amount) {
        validate(amount);
        this.balance = this.balance.subtract(amount);
    }
}
```

### Domain Event after Aggregate Change

```java
public void deposit(Money amount) {
    this.balance = this.balance.add(amount);
    registerEvent(new AccountBalanceChangedEvent(this.id, amount));
}
```

## Best Practices

- Keep aggregates easy to understand and test
- Avoid loading multiple large aggregates in a single use case
- Use repository methods that return aggregate roots only
- Keep events and services outside aggregate internals
- Model aggregate boundaries according to business rules, not persistence
