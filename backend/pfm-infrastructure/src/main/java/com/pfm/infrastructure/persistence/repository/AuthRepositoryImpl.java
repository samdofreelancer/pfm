package com.pfm.infrastructure.persistence.repository;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.domain.auth.repository.AuthRepository;
import com.pfm.infrastructure.persistence.jpa.entity.JpaAuthUserEntity;
import com.pfm.infrastructure.persistence.jpa.mapper.AuthUserPersistenceMapper;
import com.pfm.infrastructure.persistence.jpa.repository.JpaAuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final JpaAuthUserRepository jpaAuthUserRepository;
    private final AuthUserPersistenceMapper authUserPersistenceMapper;

    @Override
    public Optional<AuthUser> findById(AuthUserId id) {
        return jpaAuthUserRepository.findById(java.util.UUID.fromString(id.getValue()))
            .map(authUserPersistenceMapper::toDomainUser);
    }

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        return jpaAuthUserRepository.findByEmail(email)
            .map(authUserPersistenceMapper::toDomainUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaAuthUserRepository.existsByEmail(email);
    }

    @Override
    public AuthUser save(AuthUser authUser) {
        JpaAuthUserEntity entity = authUserPersistenceMapper.toJpaEntity(authUser);
        JpaAuthUserEntity savedEntity = jpaAuthUserRepository.save(entity);
        return authUserPersistenceMapper.toDomainUser(savedEntity);
    }

    @Override
    public void delete(AuthUserId id) {
        jpaAuthUserRepository.deleteById(java.util.UUID.fromString(id.getValue()));
    }
}