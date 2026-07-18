package com.pfm.infrastructure.persistence.jpa.repository;

import com.pfm.infrastructure.persistence.jpa.entity.JpaAuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAuthUserRepository extends JpaRepository<JpaAuthUserEntity, UUID> {
    Optional<JpaAuthUserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}