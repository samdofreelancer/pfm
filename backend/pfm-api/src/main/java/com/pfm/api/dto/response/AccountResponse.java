package com.pfm.api.dto.response;

import com.pfm.domain.account.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
    String id,
    String userId,
    AccountType type,
    String name,
    String description,
    BigDecimal balance,
    String currency,
    boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}