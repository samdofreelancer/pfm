package com.pfm.domain.user.service;

import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.repository.UserRepository;
import com.pfm.domain.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    public void assertEmailNotExists(Email email) {
        if (userRepository.existsByEmail(email)) {
            throw new DomainException("EMAIL_EXISTS", "Email already registered");
        }
    }

    public void assertUserIsActive(com.pfm.domain.user.model.User user) {
        if (!user.isActive()) {
            throw new DomainException("USER_DISABLED", "User account is disabled");
        }
    }
}
