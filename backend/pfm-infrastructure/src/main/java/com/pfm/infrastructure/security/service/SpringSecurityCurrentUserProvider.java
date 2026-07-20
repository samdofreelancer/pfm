package com.pfm.infrastructure.security.service;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.repository.AuthRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

    private final AuthRepository authRepository;

    public SpringSecurityCurrentUserProvider(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new BusinessException("UNAUTHENTICATED", "User is not authenticated", 401);
        }
        return authentication.getName();
    }

    @Override
    public String currentUserId() {
        String email = currentUserEmail();
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", 404))
                .getId()
                .getValue()
                .toString();
    }
}
