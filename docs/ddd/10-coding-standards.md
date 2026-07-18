# 📏 Coding Standards & Conventions

## Naming Conventions

### Classes

```java
// Aggregates
public class Account { ... }
public class Transaction { ... }

// Value Objects
public class Money { ... }
public class Email { ... }

// Domain Services
public class TransferService { ... }
public class BudgetCalculator { ... }

// Repositories
public interface AccountRepository { ... }

// Application Services
public class CreateTransactionCommand { ... }
public class GetAccountQuery { ... }

// Handlers
public class CreateTransactionHandler { ... }
```

### Methods

```java
// Business methods
public void withdraw(Money amount) { ... }
public void deposit(Money amount) { ... }

// Query methods
public Money getBalance() { ... }
public boolean isActive() { ... }

// Factory methods
public static Account create(...) { ... }
```

### Variables

```java
// Use meaningful names
private Money amount;  // ✅ Good
private Money amt;     // ❌ Bad
```

## Package Structure

- `com.pfm.domain.account` – domain objects and domain interfaces
- `com.pfm.domain.transaction` – transaction domain models
- `com.pfm.application.account` – account use cases
- `com.pfm.application.transaction` – transaction use cases
- `com.pfm.api.controller` – REST endpoints
- `com.pfm.infrastructure.persistence` – data access
- `com.pfm.infrastructure.security` – security support
- `com.pfm.integration.llm` – external AI integrations

## Code Style

### Domain Object Example

```java
package com.pfm.domain.account.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Account extends AggregateRoot<AccountId> {
    private final AccountId id;
    private final UserId userId;
    private AccountName name;
    private Money balance;
    private AccountStatus status;

    public void withdraw(Money amount) {
        validateAmount(amount);
        validateBalance(amount);
        this.balance = this.balance.subtract(amount);
        registerEvent(new AccountBalanceChangedEvent(id, amount.negate()));
    }

    private void validateAmount(Money amount) {
        if (amount.isNegative()) {
            throw new DomainException("Amount must be positive");
        }
    }

    private void validateBalance(Money amount) {
        if (balance.isLessThan(amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }
}
```

### Command Example

```java
package com.pfm.application.transaction.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTransactionCommand {
    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be > 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private String type; // INCOME, EXPENSE, TRANSFER

    @Size(max = 500, message = "Note must be ≤ 500 characters")
    private String note;
}
```

### Controller Example

```java
package com.pfm.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "Transaction management API")
public class TransactionController {

    private final CreateTransactionHandler createHandler;

    @PostMapping
    @Operation(summary = "Create a new transaction")
    public ResponseEntity<TransactionResponse> create(
        @Valid @RequestBody TransactionRequest request,
        Authentication auth
    ) {
        CreateTransactionCommand command = TransactionMapper.toCommand(request, auth.getName());
        Transaction transaction = createHandler.handle(command);
        return ResponseEntity.ok(TransactionMapper.toResponse(transaction));
    }
}
```

## Exception Handling

```java
public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .code(ex.getErrorCode() != null ? ex.getErrorCode().getCode() : 500)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.badRequest().body(response);
    }
}
```

## Testing Standards

### Unit Test Example

```java
@DisplayName("Account should")
@Test
void withdraw_shouldReduceBalance_whenSufficientBalance() {
    Account account = Account.builder()
        .balance(new Money(BigDecimal.valueOf(1000), Currency.getInstance("VND")))
        .build();

    account.withdraw(new Money(BigDecimal.valueOf(300), Currency.getInstance("VND")));

    assertThat(account.getBalance().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(700));
}
```

### Integration Test Example

```java
@SpringBootTest
@Transactional
class AccountRepositoryTest {
    @Autowired
    private AccountRepository repository;

    @Test
    void shouldSaveAndRetrieveAccount() {
        Account account = createSampleAccount();

        Account saved = repository.save(account);
        Account found = repository.findById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getBalance()).isEqualTo(account.getBalance());
    }
}
```

## Documentation Standards

```java
/**
 * Represents a financial account (Cash, Bank, E-wallet).
 *
 * <p>Accounts maintain balance and provide operations for deposit and withdrawal.
 * Each account belongs to a single user and can be marked as active/inactive.</p>
 *
 * <p>Invariants:
 * <ul>
 *   <li>Balance must never be negative</li>
 *   <li>Account name must be non-empty</li>
 *   <li>Only active accounts can be used in transactions</li>
 * </ul>
 *
 * @author PFM Team
 * @version 2.0
 * @see Transaction
 * @see Budget
 */
public class Account { ... }
```

## Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Code style changes
- refactor: Code refactoring
- test: Test related changes
- chore: Build/CI changes

Example:
```
feat(transaction): add AI-powered expense creation

- Add ProcessChatHandler for chat-based transaction creation
- Implement IntentParser with LLM integration
- Add confirmation flow for AI actions

Closes #123
```

## Summary

This document defines naming, package structure, code style, exception handling, and testing guidelines for the PFM project.
