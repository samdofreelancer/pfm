package com.pfm.infrastructure.persistence.jpa.mapper;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.infrastructure.persistence.jpa.entity.JpaUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthUserPersistenceMapper {

    public JpaUserEntity toJpaEntity(AuthUser authUser) {
        return JpaUserEntity.builder()
            .id(java.util.UUID.fromString(authUser.getId().toString()))
            .email(authUser.getEmail().toString())
            .password(authUser.getPassword())
            .fullName(authUser.getFullName())
            .avatarUrl(authUser.getAvatarUrl())
            .emailVerified(authUser.isEmailVerified())
            .createdAt(authUser.getCreatedAt())
            .updatedAt(authUser.getUpdatedAt())
            .deletedAt(authUser.getDeletedAt())
            .build();
    }

    public AuthUser toDomainUser(JpaUserEntity entity) {
        return AuthUser.restore(
            AuthUserId.from(entity.getId().toString()),
            entity.getEmail(),
            entity.getPassword(),
            entity.getFullName(),
            entity.getAvatarUrl(),
            entity.isEmailVerified(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );
    }
}