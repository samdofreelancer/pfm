package com.pfm.bootstrap.integration;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthRepositoryIntegrationTest {

    @Autowired
    private AuthRepository authRepository;

    @Test
    void saveAndFindByEmail_ShouldRoundTripAuthUser() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded", "John Doe");

        AuthUser saved = authRepository.save(authUser);
        AuthUser found = authRepository.findByEmail("john@example.com").orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("john@example.com", found.getEmail().getValue());
        assertEquals("John Doe", found.getFullName());
        assertTrue(found.isActive());
    }

    @Test
    void save_ShouldPersistSoftDeletedUserState() {
        AuthUser authUser = AuthUser.create("deleted@example.com", "encoded", "Deleted User");
        authUser.delete();

        authRepository.save(authUser);

        AuthUser found = authRepository.findByEmail("deleted@example.com").orElseThrow();
        assertFalse(found.isActive());
        assertNotNull(found.getDeletedAt());
    }
}
