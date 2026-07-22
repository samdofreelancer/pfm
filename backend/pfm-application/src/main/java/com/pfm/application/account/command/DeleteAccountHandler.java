package com.pfm.application.account.command;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteAccountHandler {
    private final AccountRepository accountRepository;
    private final CurrentUserProvider currentUserProvider;

    public DeleteAccountHandler(AccountRepository accountRepository, CurrentUserProvider currentUserProvider) {
        this.accountRepository = accountRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public void handle(DeleteAccountCommand command) {
        AccountId accountId = AccountId.from(command.accountId());
        AccountOwnerId ownerId = AccountOwnerId.from(currentUserProvider.currentUserId());
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("ACCOUNT_NOT_FOUND", "Account not found", 404));

        if (!account.getUserId().equals(ownerId)) {
            throw new BusinessException("ACCOUNT_FORBIDDEN", "Account does not belong to current user", 403);
        }

        account.deactivate();
        accountRepository.save(account);
    }
}
