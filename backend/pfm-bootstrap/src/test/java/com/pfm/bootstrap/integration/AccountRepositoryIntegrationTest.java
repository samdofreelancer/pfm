package com.pfm.bootstrap.integration;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.model.Money;
import com.pfm.domain.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void saveAndFindById_ShouldRoundTripAccountWithMoney() {
        Account account = Account.create(
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Wallet",
                "Daily cash",
                Money.of(new BigDecimal("123.45"), "vnd")
        );

        Account saved = accountRepository.save(account);
        Account found = accountRepository.findById(saved.getId()).orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("user-1", found.getUserId().getValue());
        assertEquals(new BigDecimal("123.45"), found.getBalance().getAmount());
        assertEquals("VND", found.getCurrency());
        assertTrue(found.isActive());
    }

    @Test
    void findActiveByUserId_ShouldExcludeInactiveAccounts() {
        Account active = Account.create(
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Active Wallet",
                null,
                Money.of(BigDecimal.TEN, "VND")
        );
        Account inactive = Account.create(
                AccountOwnerId.from("user-1"),
                AccountType.BANK_ACCOUNT,
                "Closed Bank",
                null,
                Money.of(BigDecimal.ONE, "VND")
        );
        inactive.deactivate();

        accountRepository.save(active);
        accountRepository.save(inactive);

        var accounts = accountRepository.findActiveByUserId(AccountOwnerId.from("user-1"));

        assertEquals(1, accounts.size());
        assertEquals("Active Wallet", accounts.get(0).getName());
    }
}
