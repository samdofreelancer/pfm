package com.pfm.domain.account.model;

import lombok.Value;

@Value
public class AccountOwnerId {
    String value;

    private AccountOwnerId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Account owner id must not be blank");
        }
        this.value = value;
    }

    public static AccountOwnerId from(String value) {
        return new AccountOwnerId(value);
    }
}
