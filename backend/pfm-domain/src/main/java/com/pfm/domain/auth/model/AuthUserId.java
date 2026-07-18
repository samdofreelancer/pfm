package com.pfm.domain.auth.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthUserId {
    private final String value;

    public AuthUserId() {
        this.value = UUID.randomUUID().toString();
    }

    public AuthUserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AuthUserId cannot be null or blank");
        }
        this.value = value;
    }

    public static AuthUserId generate() {
        return new AuthUserId(UUID.randomUUID().toString());
    }

    public static AuthUserId from(String value) {
        return new AuthUserId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
