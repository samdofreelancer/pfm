package com.pfm.infrastructure.persistence.jpa.repository;

import com.pfm.infrastructure.persistence.jpa.entity.JpaAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountJpaRepository extends JpaRepository<JpaAccountEntity, String> {
    List<JpaAccountEntity> findByUserId(String userId);
}