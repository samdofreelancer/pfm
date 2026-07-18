package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.RefreshTokenCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenHandler implements CommandHandler<RefreshTokenCommand, AuthResponse> {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse handle(RefreshTokenCommand command) {
        if (!tokenService.validateToken(command.getRefreshToken())) {
            throw new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired", 401);
        }

        String emailValue = tokenService.getEmailFromToken(command.getRefreshToken());
        Email email = new Email(emailValue);
        return userRepository.findByEmail(email)
            .map(user -> {
                String newAccessToken = tokenService.generateAccessToken(user);
                String newRefreshToken = tokenService.generateRefreshToken(user);
                long expiresIn = tokenService.getAccessTokenExpirationMs();
                return authMapper.toAuthResponseWithTokens(user, newAccessToken, newRefreshToken, expiresIn);
            })
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found for refresh token", 404));
    }
}
