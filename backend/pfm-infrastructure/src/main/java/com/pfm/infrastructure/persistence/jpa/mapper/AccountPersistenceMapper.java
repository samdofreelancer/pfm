package com.pfm.infrastructure.persistence.jpa.mapper;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.infrastructure.persistence.jpa.entity.JpaAccountEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountPersistenceMapper {

    public JpaAccountEntity toEntity(Account account) {
        return JpaAccountEntity.builder()
                .id(account.getId().getValue())
                .userId(account.getUserId().getValue())
                .type(account.getType())
                .name(account.getName())
                .description(account.getDescription())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .isActive(account.isActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .deletedAt(account.getDeletedAt())
                .build();
    }

    public Account toDomain(JpaAccountEntity entity) {
        return Account.restore(
                AccountId.from(entity.getId()),
                AccountId.from(entity.getUserId()),
                entity.getType(),
                entity.getName(),
                entity.getDescription(),
                entity.getBalance(),
                entity.getCurrency(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}