package com.pfm.infrastructure.persistence.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Legacy auth-specific view model retained for compatibility.
 * Auth persistence now uses JpaUserEntity as the single owner of the users table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaAuthUserEntity {
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private String avatarUrl;
    private boolean emailVerified = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}