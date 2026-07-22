package com.pfm.application.auth.handler;

import com.pfm.application.auth.dto.ProfileResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.auth.query.GetProfileQuery;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProfileHandlerTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private GetProfileHandler handler;

    @Test
    void handle_ShouldReturnCurrentUserProfile() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded", "John Doe");
        ProfileResponse expected = ProfileResponse.builder().email("john@example.com").fullName("John Doe").build();
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));
        when(authMapper.toProfileResponse(authUser)).thenReturn(expected);

        ProfileResponse response = handler.handle(new GetProfileQuery());

        assertEquals(expected, response);
        verify(authMapper).toProfileResponse(authUser);
    }

    @Test
    void handle_ShouldThrowNotFound_WhenCurrentUserDoesNotExist() {
        when(currentUserProvider.currentUserEmail()).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(new GetProfileQuery()));

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
        verify(authMapper, never()).toProfileResponse(any());
    }
}
