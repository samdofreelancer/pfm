package com.pfm.application.account.command;

import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.repository.AccountRepository;
import com.pfm.domain.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CleanupAccountsByEmailHandler {

    private final AuthRepository authRepository;
    private final AccountRepository accountRepository;

    public CleanupAccountsByEmailHandler(AuthRepository authRepository, AccountRepository accountRepository) {
        this.authRepository = authRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void handle(CleanupAccountsByEmailCommand command) {
        authRepository.findByEmail(command.email())
                .ifPresent(authUser -> accountRepository.findByUserId(AccountOwnerId.from(authUser.getId().getValue().toString()))
                        .forEach(account -> accountRepository.delete(account.getId())));
    }
}
