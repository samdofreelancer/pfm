package com.pfm.application.account.command;

import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAccountHandler {
    private final AccountRepository accountRepository;

    public DeleteAccountHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void handle(DeleteAccountCommand command) {
        AccountId accountId = AccountId.from(command.accountId());
        accountRepository.delete(accountId);
    }
}