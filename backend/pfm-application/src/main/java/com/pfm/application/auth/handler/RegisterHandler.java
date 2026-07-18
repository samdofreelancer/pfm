package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.RegisterCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.repository.UserRepository;
import com.pfm.domain.user.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterHandler implements CommandHandler<RegisterCommand, AuthResponse> {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    @Override
    @Transactional
    public AuthResponse handle(RegisterCommand command) {
        Email email = new Email(command.getEmail());

        // Check uniqueness via domain service
        userDomainService.assertEmailNotExists(email);

        // Encode password
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // Create domain User
        User user = User.create(email, encodedPassword, command.getFullName());

        // Persist
        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(savedUser);
        String refreshToken = tokenService.generateRefreshToken(savedUser);
        long expiresIn = tokenService.getAccessTokenExpirationMs();

        return authMapper.toAuthResponseWithTokens(savedUser, accessToken, refreshToken, expiresIn);
    }
}