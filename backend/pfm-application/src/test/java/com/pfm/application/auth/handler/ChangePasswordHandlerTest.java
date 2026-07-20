package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.ChangePasswordCommand;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordHandlerTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private ChangePasswordHandler handler;

    @Test
    void handle_ShouldChangePassword_WhenCurrentPasswordMatches() {
        AuthUser authUser = AuthUser.create("john@example.com", "old-hash");
        ChangePasswordCommand command = ChangePasswordCommand.builder()
                .currentPassword("old-password")
                .newPassword("new-password")
                .build();
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));
        when(passwordHasher.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordHasher.hash("new-password")).thenReturn("new-hash");

        handler.handle(command);

        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authRepository).save(userCaptor.capture());
        assertEquals("new-hash", userCaptor.getValue().getPassword());
    }

    @Test
    void handle_ShouldThrowInvalidCredentials_WhenCurrentPasswordDoesNotMatch() {
        AuthUser authUser = AuthUser.create("john@example.com", "old-hash");
        ChangePasswordCommand command = ChangePasswordCommand.builder()
                .currentPassword("wrong-password")
                .newPassword("new-password")
                .build();
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));
        when(passwordHasher.matches("wrong-password", "old-hash")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(command));

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals(400, exception.getStatus());
        verify(passwordHasher, never()).hash(any());
        verify(authRepository, never()).save(any());
    }

    @Test
    void handle_ShouldThrowNotFound_WhenCurrentUserDoesNotExist() {
        ChangePasswordCommand command = ChangePasswordCommand.builder()
                .currentPassword("old-password")
                .newPassword("new-password")
                .build();
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(command));

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
        verify(passwordHasher, never()).matches(any(), any());
    }
}
