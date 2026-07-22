package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.UpdateProfileCommand;
import com.pfm.application.auth.dto.ProfileResponse;
import com.pfm.application.auth.mapper.AuthMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileHandlerTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private UpdateProfileHandler handler;

    @Test
    void handle_ShouldUpdateCurrentUserProfile() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded", "Old Name");
        AuthUser savedUser = AuthUser.restore(
                authUser.getId(),
                authUser.getEmail().getValue(),
                authUser.getPassword(),
                "New Name",
                null,
                false,
                authUser.getCreatedAt(),
                authUser.getUpdatedAt(),
                null
        );
        ProfileResponse expected = ProfileResponse.builder().fullName("New Name").build();
        UpdateProfileCommand command = UpdateProfileCommand.builder().fullName("New Name").build();
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));
        when(authRepository.save(any(AuthUser.class))).thenReturn(savedUser);
        when(authMapper.toProfileResponse(savedUser)).thenReturn(expected);

        ProfileResponse response = handler.handle(command);

        assertEquals(expected, response);
        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authRepository).save(userCaptor.capture());
        assertEquals("New Name", userCaptor.getValue().getFullName());
    }

    @Test
    void handle_ShouldThrowNotFound_WhenCurrentUserDoesNotExist() {
        UpdateProfileCommand command = UpdateProfileCommand.builder().fullName("New Name").build();
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(command));

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
        verify(authRepository, never()).save(any());
    }
}
