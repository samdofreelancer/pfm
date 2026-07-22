package com.pfm.domain.auth.service;

import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.Email;
import com.pfm.domain.auth.repository.AuthRepository;
import com.pfm.domain.shared.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthDomainServiceTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthDomainService service;

    @Test
    void assertEmailNotExists_ShouldPass_WhenEmailIsUnique() {
        Email email = Email.from("john@example.com");
        when(authRepository.existsByEmail("john@example.com")).thenReturn(false);

        service.assertEmailNotExists(email);
    }

    @Test
    void assertEmailNotExists_ShouldThrow_WhenEmailExists() {
        Email email = Email.from("john@example.com");
        when(authRepository.existsByEmail("john@example.com")).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () -> service.assertEmailNotExists(email));

        assertEquals("EMAIL_EXISTS", exception.getCode());
    }

    @Test
    void assertUserIsActive_ShouldThrow_WhenUserIsDeleted() {
        AuthUser disabled = AuthUser.restore(
                com.pfm.domain.auth.model.AuthUserId.generate(),
                "john@example.com",
                "encoded",
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        DomainException exception = assertThrows(DomainException.class, () -> service.assertUserIsActive(disabled));

        assertEquals("USER_DISABLED", exception.getCode());
    }

    @Test
    void validateEmailFormat_ShouldAcceptValidEmail() {
        service.validateEmailFormat(Email.from("john@example.com"));
    }
}
