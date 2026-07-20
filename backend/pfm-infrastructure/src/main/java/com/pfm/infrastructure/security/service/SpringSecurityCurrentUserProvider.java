package com.pfm.infrastructure.security.service;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new BusinessException("UNAUTHENTICATED", "User is not authenticated", 401);
        }
        return authentication.getName();
    }
}
