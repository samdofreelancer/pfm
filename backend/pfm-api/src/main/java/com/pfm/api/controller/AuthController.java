package com.pfm.api.controller;

import com.pfm.api.dto.request.LoginRequest;
import com.pfm.api.dto.request.RefreshRequest;
import com.pfm.api.dto.request.RegisterRequest;
import com.pfm.application.auth.command.LoginCommand;
import com.pfm.application.auth.command.RefreshTokenCommand;
import com.pfm.application.auth.command.RegisterCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.handler.LoginHandler;
import com.pfm.application.auth.handler.RefreshTokenHandler;
import com.pfm.application.auth.handler.RegisterHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginHandler loginHandler;
    private final RegisterHandler registerHandler;
    private final RefreshTokenHandler refreshTokenHandler;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = LoginCommand.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .build();

        AuthResponse response = loginHandler.handle(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterCommand command = RegisterCommand.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .fullName(request.getFullName())
            .build();

        AuthResponse response = registerHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        RefreshTokenCommand command = RefreshTokenCommand.builder()
            .refreshToken(request.getRefreshToken())
            .build();

        AuthResponse response = refreshTokenHandler.handle(command);
        return ResponseEntity.ok(response);
    }
}