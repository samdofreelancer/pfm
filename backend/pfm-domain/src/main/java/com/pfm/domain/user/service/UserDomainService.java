package com.pfm.domain.user.service;

import com.pfm.common.exception.BusinessException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    public void assertEmailNotExists(Email email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered", 409);
        }
    }

    public void assertUserIsActive(com.pfm.domain.user.model.User user) {
        if (!user.isActive()) {
            throw new BusinessException("USER_DISABLED", "User account is disabled", 403);
        }
    }
}