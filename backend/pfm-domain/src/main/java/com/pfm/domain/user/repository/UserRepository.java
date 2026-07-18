package com.pfm.domain.user.repository;

import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.model.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    User save(User user);
    void delete(UserId id);
}