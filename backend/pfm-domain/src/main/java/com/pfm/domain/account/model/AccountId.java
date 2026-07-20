package com.pfm.domain.account.model;

import lombok.Value;

import java.util.UUID;

@Value
public class AccountId {
    String value;

    private AccountId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Account id must not be blank");
        }
        this.value = value;
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }

    public static AccountId from(String value) {
        return new AccountId(value);
    }

    public String getValue() {
        return value;
    }
}
