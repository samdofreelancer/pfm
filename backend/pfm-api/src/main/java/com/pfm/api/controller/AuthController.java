package com.pfm.api.controller;

import com.pfm.api.dto.request.LoginRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

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
        String refreshToken = response.getRefreshToken();
        response.setRefreshToken(null);

        ResponseCookie cookie = createRefreshTokenCookie(refreshToken);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterCommand command = RegisterCommand.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .fullName(request.getFullName())
            .build();

        AuthResponse response = registerHandler.handle(command);
        String refreshToken = response.getRefreshToken();
        response.setRefreshToken(null);

        ResponseCookie cookie = createRefreshTokenCookie(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        RefreshTokenCommand command = RefreshTokenCommand.builder()
            .refreshToken(refreshToken)
            .build();

        AuthResponse response = refreshTokenHandler.handle(command);
        String newRefreshToken = response.getRefreshToken();
        response.setRefreshToken(null);

        ResponseCookie cookie = createRefreshTokenCookie(newRefreshToken);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .build();
    }
}