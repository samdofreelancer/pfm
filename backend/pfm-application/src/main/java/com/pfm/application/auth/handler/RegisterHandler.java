package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.RegisterCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.Email;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import com.pfm.domain.auth.service.AuthDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterHandler implements CommandHandler<RegisterCommand, AuthResponse> {

    private final AuthRepository authRepository;
    private final AuthDomainService authDomainService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    @Override
    @Transactional
    public AuthResponse handle(RegisterCommand command) {
        Email email = Email.from(command.getEmail());

        // Check uniqueness via domain service
        authDomainService.assertEmailNotExists(email);

        // Encode password
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // Create domain AuthUser
        AuthUser authUser = AuthUser.create(email.getValue(), encodedPassword);

        // Persist
        AuthUser savedAuthUser = authRepository.save(authUser);

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(savedAuthUser);
        String refreshToken = tokenService.generateRefreshToken(savedAuthUser);
        long expiresIn = tokenService.getAccessTokenExpirationMs();

        return authMapper.toAuthResponseWithTokens(savedAuthUser, accessToken, refreshToken, expiresIn);
    }
}
