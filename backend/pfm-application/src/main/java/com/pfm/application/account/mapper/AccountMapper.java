package com.pfm.application.account.mapper;

import com.pfm.domain.account.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
            account.getId().getValue(),
            account.getUserId().getValue(),
            account.getType(),
            account.getName(),
            account.getDescription(),
            account.getBalance(),
            account.getCurrency(),
            account.isActive(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }

    public record AccountResponse(
        String id,
        String userId,
        com.pfm.domain.account.model.AccountType type,
        String name,
        String description,
        BigDecimal balance,
        String currency,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}
