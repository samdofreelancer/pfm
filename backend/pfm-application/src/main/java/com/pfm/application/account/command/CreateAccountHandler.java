package com.pfm.application.account.command;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.model.Money;
import com.pfm.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateAccountHandler {
    private final AccountRepository accountRepository;
    private final CurrentUserProvider currentUserProvider;

    public CreateAccountHandler(AccountRepository accountRepository, CurrentUserProvider currentUserProvider) {
        this.accountRepository = accountRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public AccountId handle(CreateAccountCommand command) {
        AccountOwnerId userId = AccountOwnerId.from(currentUserProvider.currentUserId());
        Account account = Account.create(
            userId,
            command.type(),
            command.name(),
            command.description(),
            Money.of(command.initialBalance() != null ? command.initialBalance() : BigDecimal.ZERO,
                    command.currency() != null ? command.currency() : "VND")
        );

        Account saved = accountRepository.save(account);
        return saved.getId();
    }
}
