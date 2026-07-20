package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.DeleteUserCommand;
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
class DeleteUserHandlerTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private DeleteUserHandler handler;

    @Test
    void handle_ShouldSoftDeleteCurrentUser() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded");
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));

        handler.handle(new DeleteUserCommand());

        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authRepository).save(userCaptor.capture());
        assertFalse(userCaptor.getValue().isActive());
        assertNotNull(userCaptor.getValue().getDeletedAt());
    }

    @Test
    void handle_ShouldThrowNotFound_WhenCurrentUserCannotBeLoaded() {
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> handler.handle(new DeleteUserCommand()));

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
        verify(authRepository, never()).save(any());
    }
}
