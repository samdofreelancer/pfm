package com.pfm.domain.user.service;

import com.pfm.domain.shared.exception.DomainException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.model.UserId;
import com.pfm.domain.user.repository.UserRepository;
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
class UserDomainServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDomainService service;

    @Test
    void assertEmailNotExists_ShouldPass_WhenEmailIsUnique() {
        Email email = new Email("john@example.com");
        when(userRepository.existsByEmail(email)).thenReturn(false);

        service.assertEmailNotExists(email);
    }

    @Test
    void assertEmailNotExists_ShouldThrow_WhenEmailExists() {
        Email email = new Email("john@example.com");
        when(userRepository.existsByEmail(email)).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class, () -> service.assertEmailNotExists(email));

        assertEquals("EMAIL_EXISTS", exception.getCode());
    }

    @Test
    void assertUserIsActive_ShouldThrow_WhenUserIsDeleted() {
        User disabled = User.restore(
                UserId.generate(),
                new Email("john@example.com"),
                "encoded",
                "John Doe",
                null,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        DomainException exception = assertThrows(DomainException.class, () -> service.assertUserIsActive(disabled));

        assertEquals("USER_DISABLED", exception.getCode());
    }
}
