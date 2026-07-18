package com.pfm.infrastructure.persistence.repository;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.domain.auth.repository.AuthRepository;
import com.pfm.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.pfm.infrastructure.persistence.jpa.mapper.AuthUserPersistenceMapper;
import com.pfm.infrastructure.persistence.jpa.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final JpaUserRepository jpaUserRepository;
    private final AuthUserPersistenceMapper authUserPersistenceMapper;

    @Override
    public Optional<AuthUser> findById(AuthUserId id) {
        return jpaUserRepository.findById(java.util.UUID.fromString(id.getValue()))
            .map(authUserPersistenceMapper::toDomainUser);
    }

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
            .map(authUserPersistenceMapper::toDomainUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public AuthUser save(AuthUser authUser) {
        JpaUserEntity entity = authUserPersistenceMapper.toJpaEntity(authUser);
        JpaUserEntity savedEntity = jpaUserRepository.save(entity);
        return authUserPersistenceMapper.toDomainUser(savedEntity);
    }

    @Override
    public void delete(AuthUserId id) {
        jpaUserRepository.deleteById(java.util.UUID.fromString(id.getValue()));
    }
}