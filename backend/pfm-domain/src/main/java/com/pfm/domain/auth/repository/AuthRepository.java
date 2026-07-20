package com.pfm.domain.auth.repository;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;

import java.util.Optional;

public interface AuthRepository {
    Optional<AuthUser> findById(AuthUserId id);
    
    Optional<AuthUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    AuthUser save(AuthUser authUser);
    
    void delete(AuthUserId id);
}
