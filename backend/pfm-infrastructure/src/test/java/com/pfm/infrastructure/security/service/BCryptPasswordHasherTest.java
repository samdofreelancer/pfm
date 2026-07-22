package com.pfm.infrastructure.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BCryptPasswordHasherTest {

    @Test
    void hash_ShouldDelegateToPasswordEncoder() {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        BCryptPasswordHasher hasher = new BCryptPasswordHasher(passwordEncoder);
        when(passwordEncoder.encode("raw")).thenReturn("hashed");

        assertTrue("hashed".equals(hasher.hash("raw")));
        verify(passwordEncoder).encode("raw");
    }

    @Test
    void matches_ShouldDelegateToPasswordEncoder() {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        BCryptPasswordHasher hasher = new BCryptPasswordHasher(passwordEncoder);
        when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertTrue(hasher.matches("raw", "hashed"));
        assertFalse(hasher.matches("wrong", "hashed"));
    }
}
