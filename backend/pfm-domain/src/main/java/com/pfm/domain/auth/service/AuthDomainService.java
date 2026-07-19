package com.pfm.domain.auth.service;

import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.Email;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AuthDomainService {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final AuthRepository authRepository;

    public void assertEmailNotExists(Email email) {
        if (authRepository.existsByEmail(email.getValue())) {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered", 409);
        }
    }

    public void assertUserIsActive(AuthUser authUser) {
        if (!authUser.isActive()) {
            throw new BusinessException("USER_DISABLED", "User account is disabled", 403);
        }
    }

    public void validateEmailFormat(Email email) {
        if (!EMAIL_PATTERN.matcher(email.getValue()).matches()) {
            throw new BusinessException("INVALID_EMAIL", "Invalid email format", 400);
        }
    }
}