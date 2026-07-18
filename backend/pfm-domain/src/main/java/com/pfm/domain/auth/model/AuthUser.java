package com.pfm.domain.auth.model;

import com.pfm.domain.shared.event.DomainEvent;
import com.pfm.domain.auth.event.AuthUserCreatedEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class AuthUser {
    private final AuthUserId id;
    private Email email;
    private String password;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private AuthUser(AuthUserId id, Email email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static AuthUser create(String email, String encodedPassword) {
        AuthUserId authUserId = AuthUserId.generate();
        AuthUser authUser = new AuthUser(authUserId, Email.from(email), encodedPassword);
        authUser.registerEvent(new AuthUserCreatedEvent(authUserId.getValue(), email));
        return authUser;
    }

    public static AuthUser restore(AuthUserId id, String email, String password,
                                   boolean emailVerified, LocalDateTime createdAt,
                                   LocalDateTime updatedAt, LocalDateTime deletedAt) {
        AuthUser authUser = new AuthUser(id, Email.from(email), password);
        authUser.emailVerified = emailVerified;
        authUser.createdAt = createdAt;
        authUser.updatedAt = updatedAt;
        authUser.deletedAt = deletedAt;
        return authUser;
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