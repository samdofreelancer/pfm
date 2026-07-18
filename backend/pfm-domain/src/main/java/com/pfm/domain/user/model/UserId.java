package com.pfm.domain.user.model;

import lombok.Value;

import java.util.UUID;

@Value
public class UserId {
    UUID value;

    public UserId() {
        this.value = UUID.randomUUID();
    }

    public UserId(UUID value) {
        this.value = value;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}