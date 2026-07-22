package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.RefreshTokenCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
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
class RefreshTokenHandlerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private RefreshTokenHandler handler;

    @Test
    void handle_ShouldIssueNewTokens_WhenRefreshTokenIsValid() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded");
        AuthResponse expected = AuthResponse.builder().accessToken("new-access").refreshToken("new-refresh").build();
        RefreshTokenCommand command = RefreshTokenCommand.builder().refreshToken("refresh-token").build();
        when(tokenService.validateToken("refresh-token")).thenReturn(true);
        when(tokenService.getEmailFromToken("refresh-token")).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));
        when(tokenService.generateAccessToken(authUser)).thenReturn("new-access");
        when(tokenService.generateRefreshToken(authUser)).thenReturn("new-refresh");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(authMapper.toAuthResponseWithTokens(authUser, "new-access", "new-refresh", 900000L)).thenReturn(expected);

        AuthResponse response = handler.handle(command);

        assertEquals(expected, response);
    }

    @Test
    void handle_ShouldThrowInvalidRefreshToken_WhenTokenIsInvalid() {
        RefreshTokenCommand command = RefreshTokenCommand.builder().refreshToken("refresh-token").build();
        when(tokenService.validateToken("refresh-token")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(command));

        assertEquals("INVALID_REFRESH_TOKEN", exception.getCode());
        assertEquals(401, exception.getStatus());
        verify(authRepository, never()).findByEmail(any());
    }

    @Test
    void handle_ShouldThrowNotFound_WhenTokenUserDoesNotExist() {
        RefreshTokenCommand command = RefreshTokenCommand.builder().refreshToken("refresh-token").build();
        when(tokenService.validateToken("refresh-token")).thenReturn(true);
        when(tokenService.getEmailFromToken("refresh-token")).thenReturn("john@example.com");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(command));

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
    }
}
