package com.pfm.application.account.query;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAccountsHandler {
    private final AccountRepository accountRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetAccountsHandler(AccountRepository accountRepository, CurrentUserProvider currentUserProvider) {
        this.accountRepository = accountRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public List<Account> handle(GetAccountsQuery query) {
        AccountOwnerId userId = AccountOwnerId.from(currentUserProvider.currentUserId());
        return accountRepository.findActiveByUserId(userId);
    }
}
