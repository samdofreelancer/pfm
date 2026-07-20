package com.pfm.application.account.query;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAccountsHandler {
    private final AccountRepository accountRepository;

    public GetAccountsHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> handle(GetAccountsQuery query) {
        AccountOwnerId userId = AccountOwnerId.from(query.userId());
        return accountRepository.findByUserId(userId);
    }
}
