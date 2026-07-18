# 🔗 Dependency Rules & Layer Architecture

## Overview

PFM follows Clean Architecture principles with strict dependency rules. Dependencies point inward toward the domain layer.

## Dependency Direction

- Presentation layer depends on Application layer
- Application layer depends on Domain layer
- Infrastructure layer depends on Domain layer
- Domain layer depends on nothing except itself

## Layer Dependencies

### Allowed Dependencies
- Domain → Nothing
- Application → Domain
- Infrastructure → Domain
- Presentation → Application

### Forbidden Dependencies
- Domain → Infrastructure
- Domain → Application
- Application → Infrastructure directly
- Presentation → Domain directly
- Infrastructure → Presentation

## Layer Examples

### Domain Layer (pfm-domain)

```java
package com.pfm.domain.account.model;

public class Account {
    private AccountId id;
    private Money balance;

    public void withdraw(Money amount) {
        if (balance.isLessThan(amount)) {
            throw new InsufficientBalanceException();
        }
        this.balance = this.balance.subtract(amount);
    }
}
```

### Application Layer (pfm-application)

```java
package com.pfm.application.account.handler;

@Service
public class CreateAccountHandler {
    private final AccountRepository accountRepository;

    @Transactional
    public Account handle(CreateAccountCommand command) {
        Account account = new Account(...);
        return accountRepository.save(account);
    }
}
```

### Infrastructure Layer (pfm-infrastructure)

```java
package com.pfm.infrastructure.persistence.repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final JpaAccountRepository jpaRepository;

    @Override
    public Account save(Account account) {
        JpaAccountEntity entity = mapper.toEntity(account);
        JpaAccountEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

## Package Dependencies by Module

| Module | Depends On | Cannot Depend On |
|--------|------------|------------------|
| pfm-domain | pfm-common | pfm-application, pfm-infrastructure, pfm-api |
| pfm-application | pfm-domain, pfm-common | pfm-infrastructure, pfm-api |
| pfm-infrastructure | pfm-domain, pfm-application | pfm-api |
| pfm-integration | pfm-domain, pfm-common | pfm-application (except via events) |

## Guidelines

- Domain depends on nothing outside itself and shared kernel modules
- Application depends on domain and common modules only
- Infrastructure implements domain interfaces without leaking persistence details
- API depends on application and uses DTOs for external boundaries
- Avoid circular dependencies across modules
- Use events to integrate across bounded contexts

## Dependency Injection

- Use interfaces, not concrete classes, for constructor injection
- Keep wiring in the application or infrastructure configuration
- Avoid injecting repositories or entities directly into controllers

## Best Practices

- Keep the domain model clean and framework-agnostic
- Use DTOs and mappers at the boundary between application and presentation
- Keep infrastructure concerns out of the domain
- Favor event-based integration over direct module coupling
