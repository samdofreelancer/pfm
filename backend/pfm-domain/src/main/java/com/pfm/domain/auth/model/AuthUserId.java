package com.pfm.domain.auth.model;

import lombok.Value;

import java.util.UUID;

@Value
public class AuthUserId {
    UUID value;

    public AuthUserId() {
        this.value = UUID.randomUUID();
    }

    public AuthUserId(UUID value) {
        this.value = value;
    }

    public static AuthUserId generate() {
        return new AuthUserId(UUID.randomUUID());
    }

    public static AuthUserId from(String value) {
        return new AuthUserId(UUID.fromString(value));
    }
}