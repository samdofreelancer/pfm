package com.pfm.domain.user.model;

import com.pfm.domain.shared.event.DomainEvent;
import com.pfm.domain.user.event.UserRegisteredEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class User {
    private final UserId id;
    private Email email;
    private String password;
    private String fullName;
    private String avatarUrl;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private User(UserId id, Email email, String password, String fullName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User create(Email email, String encodedPassword, String fullName) {
        UserId userId = UserId.generate();
        User user = new User(userId, email, encodedPassword, fullName);
        user.registerEvent(new UserRegisteredEvent(userId.getValue(), email.getValue()));
        return user;
    }

    public static User restore(UserId id, Email email, String password, String fullName,
                                String avatarUrl, boolean emailVerified,
                                LocalDateTime createdAt, LocalDateTime updatedAt,
                                LocalDateTime deletedAt) {
        User user = new User(id, email, password, fullName);
        user.avatarUrl = avatarUrl;
        user.emailVerified = emailVerified;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.deletedAt = deletedAt;
        return user;
    }

    public void updateProfile(String fullName, String avatarUrl) {
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return deletedAt == null;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}