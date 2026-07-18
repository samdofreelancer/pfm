package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.LoginCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.model.UserId;
import com.pfm.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginHandler loginHandler;

    private LoginCommand command;
    private User user;

    @BeforeEach
    void setUp() {
        command = LoginCommand.builder()
            .email("john@example.com")
            .password("password123")
            .build();

        UUID userId = UUID.randomUUID();
        Email email = new Email("john@example.com");
        String encodedPassword = "encodedPassword";

        user = User.restore(
            new UserId(userId),
            email,
            encodedPassword,
            "John Doe",
            null,
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );
    }

    @Test
    void handle_ShouldLoginUser_WhenValidCredentials() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.getPassword(), user.getPassword())).thenReturn(true);
        when(tokenService.generateAccessToken(user)).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(user)).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse expectedResponse = AuthResponse.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .expiresIn(900000L)
            .build();

        when(authMapper.toAuthResponseWithTokens(user, "accessToken", "refreshToken", 900000L))
            .thenReturn(expectedResponse);

        // Act
        AuthResponse response = loginHandler.handle(command);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(900000L, response.getExpiresIn());

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder).matches(command.getPassword(), user.getPassword());
        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken(user);
        verify(authMapper).toAuthResponseWithTokens(user, "accessToken", "refreshToken", 900000L);
    }

    @Test
    void handle_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginHandler.handle(command);
        });

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(401, exception.getStatus());

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginHandler.handle(command);
        });

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(401, exception.getStatus());

        verify(userRepository).findByEmail(any(Email.class));
        verify(passwordEncoder).matches(command.getPassword(), user.getPassword());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldThrowException_WhenUserIsDisabled() {
        // Arrange
        User disabledUser = User.restore(
            new UserId(UUID.randomUUID()),
            new Email("john@example.com"),
            "encodedPassword",
            "John Doe",
            null,
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginHandler.handle(command);
        });

        assertEquals("USER_DISABLED", exception.getCode());
        assertEquals("User account is disabled", exception.getMessage());
        assertEquals(403, exception.getStatus());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldGenerateTokens_WhenLoginSuccessful() {
        // Arrange
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        long expiresIn = 900000L;

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.getPassword(), user.getPassword())).thenReturn(true);
        when(tokenService.generateAccessToken(user)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(expiresIn);

        when(authMapper.toAuthResponseWithTokens(user, accessToken, refreshToken, expiresIn))
            .thenReturn(AuthResponse.builder().build());

        // Act
        loginHandler.handle(command);

        // Assert
        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken(user);
        verify(tokenService).getAccessTokenExpirationMs();
    }

    @Test
    void handle_ShouldNotCheckPassword_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> loginHandler.handle(command));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}