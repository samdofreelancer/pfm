# 🎯 Application Services (Use Cases)

## Overview

Application services orchestrate use cases by coordinating domain objects and infrastructure. They represent the operations the system can perform.

## Structure

Application services typically implement commands and queries:
- Command handlers for state-changing operations
- Query handlers for read-only retrieval

## Example: CreateTransactionHandler

```java
package com.pfm.application.transaction.handler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateTransactionHandler {

    private final TransactionDomainService transactionService;
    private final AccountDomainService accountService;
    private final BudgetDomainService budgetService;
    private final CategoryRepository categoryRepository;
    private final DomainEventPublisher eventPublisher;

    public Transaction handle(CreateTransactionCommand command) {
        validate(command);

        Account account = accountService.findById(new AccountId(command.getAccountId()));
        Category category = categoryRepository.findById(new CategoryId(command.getCategoryId()));

        Transaction transaction = Transaction.builder()
            .id(TransactionId.generate())
            .accountId(account.getId())
            .categoryId(category.getId())
            .amount(new Money(command.getAmount(), Currency.getInstance("VND")))
            .type(TransactionType.valueOf(command.getType()))
            .note(command.getNote())
            .transactionDate(command.getTransactionDate())
            .status(TransactionStatus.COMPLETED)
            .build();

        if (transaction.getType() == TransactionType.EXPENSE) {
            account.withdraw(transaction.getAmount());
        } else {
            account.deposit(transaction.getAmount());
        }

        Transaction saved = transactionRepository.save(transaction);
        accountRepository.save(account);

        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateSpentAmount(command.getUserId(), category.getId(), transaction.getAmount());
        }

        transaction.getEvents().forEach(eventPublisher::publish);
        account.getEvents().forEach(eventPublisher::publish);

        return saved;
    }
}
```

## Example: AI Chat Use Case

```java
package com.pfm.application.ai.handler;

@Service
@Slf4j
public class ProcessChatHandler implements CommandHandler<ProcessChatCommand, ChatResponse> {

    private final IntentParser intentParser;
    private final ActionExecutor actionExecutor;
    private final LLMClient llmClient;
    private final ChatHistoryRepository chatHistoryRepository;

    @Override
    public ChatResponse handle(ProcessChatCommand command) {
        List<ChatMessage> context = getChatContext(command.getSessionId());
        Intent intent = intentParser.parse(command.getMessage(), context);

        if (intent.getIntentType() == IntentType.ADD_EXPENSE) {
            CreateTransactionCommand txCommand = buildTransactionCommand(intent, command.getUserId());
            Transaction tx = transactionHandler.handle(txCommand);
            String response = llmClient.generateResponse(
                "I've added your expense: " + tx.getAmount() + " in " + tx.getCategory()
            );
            return ChatResponse.success(response, tx.getId());
        }

        // ... other intents
        return ChatResponse.empty();
    }
}
```

## Guidelines for Application Services

- Single responsibility – one handler per use case
- No business logic – delegate to domain services and aggregates
- Transaction management at the application boundary using `@Transactional`
- Separate commands from queries
- Validate input parameters before use
- Publish domain events after state changes
- Keep application services thin and orchestrating only

## Application Service Types

- Command handlers: `CreateTransactionHandler`, `UpdateAccountHandler`, `DeleteBudgetHandler`, `ContributeToGoalHandler`
- Query handlers: `GetAccountHandler`, `SearchTransactionsHandler`, `GetBudgetStatusHandler`, `GetGoalProgressHandler`
- Event handlers: `TransactionCreatedEventListener`, `BudgetExceededEventListener`, `GoalAchievedEventListener`
