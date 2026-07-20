package com.pfm.domain.auth.model;

import lombok.Value;

import java.util.regex.Pattern;

@Value
public class Email {
    String value;

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        this.value = value.toLowerCase().trim();
    }

    public static Email from(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
