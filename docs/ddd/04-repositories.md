# 🗄️ Repository Pattern

## Overview

Repositories provide an abstraction for data access, hiding persistence details from the domain layer. In DDD, repository interfaces belong to the domain layer, while implementations belong to the infrastructure layer.

## Repository Structure

### Domain Layer
- `AccountRepository`
- `TransactionRepository`
- `BudgetRepository`
- `GoalRepository`
- `CategoryRepository`

### Infrastructure Layer
- `JpaAccountRepository`
- `AccountRepositoryImpl`
- `AccountPersistenceMapper`

## Repository Interfaces

### AccountRepository

```java
package com.pfm.domain.account.repository;

import java.util.List;

public interface AccountRepository {
    Account findById(AccountId id);
    List<Account> findByUserId(UserId userId);
    List<Account> findActiveByUserId(UserId userId);
    Account save(Account account);
    void delete(AccountId id);
    boolean existsById(AccountId id);
}
```

### TransactionRepository

```java
package com.pfm.domain.transaction.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository {
    Transaction findById(TransactionId id);
    List<Transaction> findByAccountId(AccountId accountId);
    List<Transaction> findByUserId(UserId userId);
    List<Transaction> findByDateRange(UserId userId, DateRange dateRange);
    Page<Transaction> findByCriteria(TransactionSearchCriteria criteria, Pageable pageable);
    Transaction save(Transaction transaction);
    void delete(TransactionId id);
}
```

## Repository Implementation Example

### AccountRepositoryImpl

```java
package com.pfm.infrastructure.persistence.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    public AccountRepositoryImpl(
        JpaAccountRepository jpaRepository,
        AccountPersistenceMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Account findById(AccountId id) {
        JpaAccountEntity entity = jpaRepository.findById(id.getValue())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return mapper.toDomain(entity);
    }

    @Override
    public List<Account> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Account save(Account account) {
        JpaAccountEntity entity = mapper.toEntity(account);
        JpaAccountEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(AccountId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(AccountId id) {
        return jpaRepository.existsById(id.getValue());
    }
}
```

### JpaAccountEntity

```java
package com.pfm.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class JpaAccountEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(length = 3)
    private String currency = "VND";

    @Column(name = "is_active")
    private boolean active = true;

    @Version
    @Column(name = "version")
    private Long version;

    // getters and setters
}
```

### JpaAccountRepository

```java
package com.pfm.infrastructure.persistence.jpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAccountRepository extends JpaRepository<JpaAccountEntity, String> {

    @Query("SELECT a FROM JpaAccountEntity a WHERE a.userId = :userId AND a.active = true")
    List<JpaAccountEntity> findActiveByUserId(@Param("userId") String userId);

    List<JpaAccountEntity> findByUserId(String userId);
}
```

### AccountPersistenceMapper

```java
package com.pfm.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {

    public JpaAccountEntity toEntity(Account domain) {
        JpaAccountEntity entity = new JpaAccountEntity();
        entity.setId(domain.getId().getValue());
        entity.setUserId(domain.getUserId().getValue());
        entity.setAccountName(domain.getName().getValue());
        entity.setBalance(domain.getBalance().getAmount());
        entity.setCurrency(domain.getBalance().getCurrency().getCurrencyCode());
        entity.setActive(domain.getStatus() == AccountStatus.ACTIVE);
        return entity;
    }

    public Account toDomain(JpaAccountEntity entity) {
        return Account.builder()
            .id(new AccountId(entity.getId()))
            .userId(new UserId(entity.getUserId()))
            .name(new AccountName(entity.getAccountName()))
            .balance(new Money(entity.getBalance(), Currency.getInstance(entity.getCurrency())))
            .status(entity.isActive() ? AccountStatus.ACTIVE : AccountStatus.INACTIVE)
            .build();
    }
}
```

## Guidelines for Repositories

- Repository interfaces belong in the domain layer
- Implementations belong in the infrastructure layer
- One repository per aggregate root
- Repositories should contain no business logic, only data access operations
- Use DTO/entity mapping to protect the domain model from persistence details
- Support pagination and sorting for query methods
- Handle optimistic locking with `@Version`

## Common Pitfalls

- ❌ Don’t place infrastructure concerns inside the domain layer
- ✅ Domain interfaces should remain persistence-agnostic
- ❌ Don’t use JPA entities directly in the application layer
- ✅ Map entities to domain objects before returning data
- ❌ Don’t forget to translate persistence exceptions into domain-level errors
