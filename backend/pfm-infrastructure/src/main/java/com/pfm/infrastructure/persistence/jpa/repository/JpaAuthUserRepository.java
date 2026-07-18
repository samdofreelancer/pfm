package com.pfm.infrastructure.persistence.jpa.repository;

import com.pfm.infrastructure.persistence.jpa.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAuthUserRepository extends JpaRepository<JpaUserEntity, UUID> {
    Optional<JpaUserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}