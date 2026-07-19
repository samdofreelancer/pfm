package com.pfm.application.account.command;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateAccountHandler {
    private final AccountRepository accountRepository;

    public CreateAccountHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountId handle(CreateAccountCommand command) {
        AccountId userId = AccountId.from(command.userId());
        Account account = Account.create(
            userId,
            command.type(),
            command.name(),
            command.description(),
            command.initialBalance() != null ? command.initialBalance() : BigDecimal.ZERO,
            command.currency() != null ? command.currency() : "VND"
        );

        Account saved = accountRepository.save(account);
        return saved.getId();
    }
}