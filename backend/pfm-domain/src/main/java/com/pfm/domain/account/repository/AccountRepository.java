package com.pfm.domain.account.repository;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(AccountId id);

    List<Account> findByUserId(AccountId userId);

    void delete(AccountId id);

    boolean existsById(AccountId id);
}