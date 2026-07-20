package com.pfm.application.account.mapper;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapper();

    @Test
    void toResponse_ShouldFlattenMoneyValueObject() {
        Account account = Account.restore(
                AccountId.from("account-1"),
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Wallet",
                "Daily cash",
                Money.of(new BigDecimal("123.45"), "VND"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        AccountMapper.AccountResponse response = mapper.toResponse(account);

        assertEquals("account-1", response.id());
        assertEquals("user-1", response.userId());
        assertEquals(new BigDecimal("123.45"), response.balance());
        assertEquals("VND", response.currency());
        assertTrue(response.isActive());
    }
}
