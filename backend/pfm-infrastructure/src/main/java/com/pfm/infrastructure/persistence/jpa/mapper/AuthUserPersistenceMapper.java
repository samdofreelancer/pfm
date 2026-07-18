package com.pfm.infrastructure.persistence.jpa.mapper;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.infrastructure.persistence.jpa.entity.JpaAuthUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthUserPersistenceMapper {

    public JpaAuthUserEntity toJpaEntity(AuthUser authUser) {
        return JpaAuthUserEntity.builder()
            .id(java.util.UUID.fromString(authUser.getId().toString()))
            .email(authUser.getEmail().toString())
            .password(authUser.getPassword())
            .emailVerified(authUser.isEmailVerified())
            .createdAt(authUser.getCreatedAt())
            .updatedAt(authUser.getUpdatedAt())
            .deletedAt(authUser.getDeletedAt())
            .build();
    }

    public AuthUser toDomainUser(JpaAuthUserEntity entity) {
        return AuthUser.restore(
            AuthUserId.from(entity.getId().toString()),
            entity.getEmail(),
            entity.getPassword(),
            entity.isEmailVerified(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );
    }
}