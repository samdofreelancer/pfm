package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.LoginCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.Email;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.domain.auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginHandler loginHandler;

    private LoginCommand command;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        command = LoginCommand.builder()
            .email("john@example.com")
            .password("password123")
            .build();

        String userId = UUID.randomUUID().toString();
        Email email = new Email("john@example.com");
        String encodedPassword = "encodedPassword";

        authUser = AuthUser.restore(
            AuthUserId.from(userId),
            email.getValue(),
            encodedPassword,
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );
    }

    @Test
    void handle_ShouldLoginUser_WhenValidCredentials() {
        // Arrange
        when(authRepository.findByEmail(anyString())).thenReturn(Optional.of(authUser));
        when(passwordHasher.matches(command.getPassword(), authUser.getPassword())).thenReturn(true);
        when(tokenService.generateAccessToken(authUser)).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(authUser)).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse expectedResponse = AuthResponse.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .expiresIn(900000L)
            .build();

        when(authMapper.toAuthResponseWithTokens(authUser, "accessToken", "refreshToken", 900000L))
            .thenReturn(expectedResponse);

        // Act
        AuthResponse response = loginHandler.handle(command);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(900000L, response.getExpiresIn());

        verify(authRepository).findByEmail(anyString());
        verify(passwordHasher).matches(command.getPassword(), authUser.getPassword());
        verify(tokenService).generateAccessToken(authUser);
        verify(tokenService).generateRefreshToken(authUser);
        verify(authMapper).toAuthResponseWithTokens(authUser, "accessToken", "refreshToken", 900000L);
    }

    @Test
    void handle_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(authRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginHandler.handle(command);
        });

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(401, exception.getStatus());

        verify(authRepository).findByEmail(anyString());
        verify(passwordHasher, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        when(authRepository.findByEmail(anyString())).thenReturn(Optional.of(authUser));
        when(passwordHasher.matches(command.getPassword(), authUser.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginHandler.handle(command);
        });

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(401, exception.getStatus());

        verify(authRepository).findByEmail(anyString());
        verify(passwordHasher).matches(command.getPassword(), authUser.getPassword());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldThrowException_WhenUserIsDisabled() {
        // Arrange
        AuthUser disabledAuthUser = AuthUser.restore(
            AuthUserId.from(UUID.randomUUID().toString()),
            "john@example.com",
            "encodedPassword",
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(authRepository.findByEmail(anyString())).thenReturn(Optional.of(disabledAuthUser));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginHandler.handle(command);
        });

        assertEquals("USER_DISABLED", exception.getCode());
        assertEquals("User account is disabled", exception.getMessage());
        assertEquals(403, exception.getStatus());

        verify(passwordHasher, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldGenerateTokens_WhenLoginSuccessful() {
        // Arrange
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        long expiresIn = 900000L;

        when(authRepository.findByEmail(anyString())).thenReturn(Optional.of(authUser));
        when(passwordHasher.matches(command.getPassword(), authUser.getPassword())).thenReturn(true);
        when(tokenService.generateAccessToken(authUser)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(authUser)).thenReturn(refreshToken);
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(expiresIn);

        when(authMapper.toAuthResponseWithTokens(authUser, accessToken, refreshToken, expiresIn))
            .thenReturn(AuthResponse.builder().build());

        // Act
        loginHandler.handle(command);

        // Assert
        verify(tokenService).generateAccessToken(authUser);
        verify(tokenService).generateRefreshToken(authUser);
        verify(tokenService).getAccessTokenExpirationMs();
    }

    @Test
    void handle_ShouldNotCheckPassword_WhenUserNotFound() {
        // Arrange
        when(authRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> loginHandler.handle(command));

        verify(passwordHasher, never()).matches(anyString(), anyString());
    }
}
