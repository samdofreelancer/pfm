package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.LoginCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.Email;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginHandler implements CommandHandler<LoginCommand, AuthResponse> {

    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse handle(LoginCommand command) {
        Email email = Email.from(command.getEmail());

        // Find user
        AuthUser authUser = authRepository.findByEmail(email.getValue())
            .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid email or password", 401));

        // Check if active
        if (!authUser.isActive()) {
            throw new BusinessException("USER_DISABLED", "User account is disabled", 403);
        }

        // Verify password
        if (!passwordHasher.matches(command.getPassword(), authUser.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password", 401);
        }

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(authUser);
        String refreshToken = tokenService.generateRefreshToken(authUser);
        long expiresIn = tokenService.getAccessTokenExpirationMs();

        return authMapper.toAuthResponseWithTokens(authUser, accessToken, refreshToken, expiresIn);
    }
}
