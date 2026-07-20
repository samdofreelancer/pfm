package com.pfm.infrastructure.persistence.jpa.mapper;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.model.Money;
import com.pfm.infrastructure.persistence.jpa.entity.JpaAccountEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountPersistenceMapperTest {

    private final AccountPersistenceMapper mapper = new AccountPersistenceMapper();

    @Test
    void toEntity_ShouldFlattenMoneyForPersistence() {
        Account account = Account.restore(
                AccountId.from("account-1"),
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Wallet",
                null,
                Money.of(new BigDecimal("100.00"), "VND"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        JpaAccountEntity entity = mapper.toEntity(account);

        assertEquals("account-1", entity.getId());
        assertEquals("user-1", entity.getUserId());
        assertEquals(new BigDecimal("100.00"), entity.getBalance());
        assertEquals("VND", entity.getCurrency());
    }

    @Test
    void toDomain_ShouldRehydrateMoneyValueObject() {
        JpaAccountEntity entity = JpaAccountEntity.builder()
                .id("account-1")
                .userId("user-1")
                .type(AccountType.CASH)
                .name("Wallet")
                .balance(new BigDecimal("100.00"))
                .currency("vnd")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Account account = mapper.toDomain(entity);

        assertEquals(new BigDecimal("100.00"), account.getBalance().getAmount());
        assertEquals("VND", account.getCurrency());
        assertTrue(account.isActive());
    }
}
