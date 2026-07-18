package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.RegisterCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.repository.UserRepository;
import com.pfm.domain.user.service.UserDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainService userDomainService;

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
        UUID userId = UUID.randomUUID();
        Email email = new Email(command.getEmail());

        User user = User.create(email, encodedPassword, command.getFullName());
        User savedUser = User.restore(
            new com.pfm.domain.user.model.UserId(userId),
            email,
            encodedPassword,
            command.getFullName(),
            null,
            false,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now(),
            null
        );

        when(passwordEncoder.encode(command.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.generateAccessToken(savedUser)).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(savedUser)).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse expectedResponse = AuthResponse.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .expiresIn(900000L)
            .build();

        when(authMapper.toAuthResponseWithTokens(savedUser, "accessToken", "refreshToken", 900000L))
            .thenReturn(expectedResponse);

        // Act
        AuthResponse response = registerHandler.handle(command);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(900000L, response.getExpiresIn());

        verify(userDomainService).assertEmailNotExists(any(Email.class));
        verify(passwordEncoder).encode(command.getPassword());
        verify(userRepository).save(any(User.class));
        verify(tokenService).generateAccessToken(savedUser);
        verify(tokenService).generateRefreshToken(savedUser);
        verify(authMapper).toAuthResponseWithTokens(savedUser, "accessToken", "refreshToken", 900000L);
    }

    @Test
    void handle_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        doThrow(new BusinessException("Email already exists", "EMAIL_EXISTS", 409))
            .when(userDomainService).assertEmailNotExists(any(Email.class));

        // Act & Assert
        assertThrows(BusinessException.class, () -> registerHandler.handle(command));

        verify(userRepository, never()).save(any());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void handle_ShouldEncodePassword_WhenRegistering() {
        // Arrange
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(command.getPassword())).thenReturn(encodedPassword);

        User user = User.create(new Email(command.getEmail()), encodedPassword, command.getFullName());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenService.generateAccessToken(any())).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(any())).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        when(authMapper.toAuthResponseWithTokens(any(), anyString(), anyString(), anyLong()))
            .thenReturn(AuthResponse.builder().build());

        // Act
        registerHandler.handle(command);

        // Assert
        verify(passwordEncoder).encode(command.getPassword());
        verify(userRepository).save(argThat(savedUser ->
            savedUser.getPassword().equals(encodedPassword)
        ));
    }

    @Test
    void handle_ShouldCreateUserWithCorrectData_WhenValidCommand() {
        // Arrange
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(command.getPassword())).thenReturn(encodedPassword);

        User user = User.create(new Email(command.getEmail()), encodedPassword, command.getFullName());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenService.generateAccessToken(any())).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(any())).thenReturn("refreshToken");
        when(tokenService.getAccessTokenExpirationMs()).thenReturn(900000L);

        when(authMapper.toAuthResponseWithTokens(any(), anyString(), anyString(), anyLong()))
            .thenReturn(AuthResponse.builder().build());

        // Act
        registerHandler.handle(command);

        // Assert
        verify(userRepository).save(argThat(savedUser -> {
            assertEquals(command.getFullName(), savedUser.getFullName());
            assertEquals(command.getEmail(), savedUser.getEmail().getValue());
            assertFalse(savedUser.isEmailVerified());
            assertNotNull(savedUser.getCreatedAt());
            assertNotNull(savedUser.getUpdatedAt());
            return true;
        }));
    }
}