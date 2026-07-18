package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.RegisterCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.Email;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.domain.auth.repository.AuthRepository;
import com.pfm.domain.auth.service.AuthDomainService;
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
class RegisterHandlerTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthDomainService authDomainService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RegisterHandler registerHandler;

    private RegisterCommand command;

    @BeforeEach
    void setUp() {
        command = RegisterCommand.builder()
            .email("john@example.com")
            .password("password123")
            .fullName("John Doe")
            .build();
    }

    @Test
    void handle_ShouldRegisterUser_WhenValidCommand() {
        // Arrange
        String encodedPassword = "encodedPassword";
        String userId = UUID.randomUUID().toString();
        Email email = new Email(command.getEmail());

        AuthUser authUser = AuthUser.create(email.getValue(), encodedPassword);
        AuthUser savedAuthUser = AuthUser.restore(
            new AuthUserId(userId),
            email.getValue(),
            encodedPassword,
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        when(passwordEncoder.encode(command.getPassword())).thenReturn(encodedPassword);
        when(authRepository.save(any(AuthUser.class))).thenReturn(savedAuthUser);
        when(tokenService.generateAccessToken(savedAuthUser)).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(savedAuthUser)).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse expectedResponse = AuthResponse.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .expiresIn(900000L)
            .build();

        when(authMapper.toAuthResponseWithTokens(savedAuthUser, "accessToken", "refreshToken", 900000L))
            .thenReturn(expectedResponse);

        // Act
        AuthResponse response = registerHandler.handle(command);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(900000L, response.getExpiresIn());

        verify(authDomainService).assertEmailNotExists(any(Email.class));
        verify(passwordEncoder).encode(command.getPassword());
        verify(authRepository).save(any(AuthUser.class));
        verify(tokenService).generateAccessToken(savedAuthUser);
        verify(tokenService).generateRefreshToken(savedAuthUser);
        verify(authMapper).toAuthResponseWithTokens(savedAuthUser, "accessToken", "refreshToken", 900000L);
    }

    @Test
    void handle_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        doThrow(new BusinessException("Email already exists", "EMAIL_EXISTS", 409))
            .when(authDomainService).assertEmailNotExists(any(Email.class));

        // Act & Assert
        assertThrows(BusinessException.class, () -> registerHandler.handle(command));

        verify(authRepository, never()).save(any());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldEncodePassword_WhenRegistering() {
        // Arrange
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(command.getPassword())).thenReturn(encodedPassword);

        AuthUser authUser = AuthUser.create(command.getEmail(), encodedPassword);
        when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
        when(tokenService.generateAccessToken(any())).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(any())).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        when(authMapper.toAuthResponseWithTokens(any(), anyString(), anyString(), anyLong()))
            .thenReturn(AuthResponse.builder().build());

        // Act
        registerHandler.handle(command);

        // Assert
        verify(passwordEncoder).encode(command.getPassword());
        verify(authRepository).save(argThat(savedUser ->
            savedUser.getPassword().equals(encodedPassword)
        ));
    }

    @Test
    void handle_ShouldCreateUserWithCorrectData_WhenValidCommand() {
        // Arrange
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(command.getPassword())).thenReturn(encodedPassword);

        AuthUser authUser = AuthUser.create(command.getEmail(), encodedPassword);
        when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
        when(tokenService.generateAccessToken(any())).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(any())).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        when(authMapper.toAuthResponseWithTokens(any(), anyString(), anyString(), anyLong()))
            .thenReturn(AuthResponse.builder().build());

        // Act
        registerHandler.handle(command);

        // Assert
        verify(authRepository).save(argThat(savedUser -> {
            assertEquals(command.getEmail(), savedUser.getEmail().getValue());
            assertEquals(command.getFullName(), savedUser.getFullName());
            assertFalse(savedUser.isEmailVerified());
            assertNotNull(savedUser.getCreatedAt());
            assertNotNull(savedUser.getUpdatedAt());
            return true;
        }));
    }
}