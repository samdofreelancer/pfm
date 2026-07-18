package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.LoginCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginHandler implements CommandHandler<LoginCommand, AuthResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse handle(LoginCommand command) {
        Email email = new Email(command.getEmail());

        // Find user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid email or password", 401));

        // Check if active
        if (!user.isActive()) {
            throw new BusinessException("USER_DISABLED", "User account is disabled", 403);
        }

        // Verify password
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password", 401);
        }

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        long expiresIn = tokenService.getAccessTokenExpirationMs();

        return authMapper.toAuthResponseWithTokens(user, accessToken, refreshToken, expiresIn);
    }
}