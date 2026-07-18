package com.pfm.infrastructure.persistence.jpa.mapper;

import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.model.UserId;
import com.pfm.infrastructure.persistence.jpa.entity.JpaUserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public JpaUserEntity toJpaEntity(User domainUser) {
        if (domainUser == null) {
            return null;
        }

        return JpaUserEntity.builder()
            .id(domainUser.getId().getValue())
            .email(domainUser.getEmail().getValue())
            .password(domainUser.getPassword())
            .fullName(domainUser.getFullName())
            .avatarUrl(domainUser.getAvatarUrl())
            .emailVerified(domainUser.isEmailVerified())
            .createdAt(domainUser.getCreatedAt())
            .updatedAt(domainUser.getUpdatedAt())
            .deletedAt(domainUser.getDeletedAt())
            .build();
    }

    public User toDomainUser(JpaUserEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return User.restore(
            new UserId(jpaEntity.getId()),
            new Email(jpaEntity.getEmail()),
            jpaEntity.getPassword(),
            jpaEntity.getFullName(),
            jpaEntity.getAvatarUrl(),
            jpaEntity.isEmailVerified(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt(),
            jpaEntity.getDeletedAt()
        );
    }
}