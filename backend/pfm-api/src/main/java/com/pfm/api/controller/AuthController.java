package com.pfm.api.controller;

import com.pfm.api.dto.request.ChangePasswordRequest;
import com.pfm.api.dto.request.DeleteUserRequest;
import com.pfm.api.dto.request.LoginRequest;
import com.pfm.api.dto.request.RegisterRequest;
import com.pfm.api.dto.request.UpdateProfileRequest;
import com.pfm.application.auth.command.ChangePasswordCommand;
import com.pfm.application.auth.command.DeleteUserCommand;
import com.pfm.application.auth.command.LoginCommand;
import com.pfm.application.auth.command.RefreshTokenCommand;
import com.pfm.application.auth.command.RegisterCommand;
import com.pfm.application.auth.command.UpdateProfileCommand;
import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.application.auth.dto.ProfileResponse;
import com.pfm.application.auth.handler.ChangePasswordHandler;
import com.pfm.application.auth.handler.DeleteUserHandler;
import com.pfm.application.auth.handler.GetProfileHandler;
import com.pfm.application.auth.handler.LoginHandler;
import com.pfm.application.auth.handler.RefreshTokenHandler;
import com.pfm.application.auth.handler.RegisterHandler;
import com.pfm.application.auth.handler.UpdateProfileHandler;
import com.pfm.application.auth.query.GetProfileQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final DeleteUserHandler deleteUserHandler;
    private final GetProfileHandler getProfileHandler;
    private final UpdateProfileHandler updateProfileHandler;
    private final ChangePasswordHandler changePasswordHandler;

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

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile() {
        GetProfileQuery query = new GetProfileQuery();
        ProfileResponse response = getProfileHandler.handle(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UpdateProfileCommand command = UpdateProfileCommand.builder()
            .fullName(request.getFullName())
            .build();

        ProfileResponse response = updateProfileHandler.handle(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        ChangePasswordCommand command = ChangePasswordCommand.builder()
            .currentPassword(request.getCurrentPassword())
            .newPassword(request.getNewPassword())
            .build();

        changePasswordHandler.handle(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        DeleteUserCommand command = DeleteUserCommand.builder()
            .email(request.getEmail())
            .build();

        deleteUserHandler.handle(command);
        return ResponseEntity.noContent().build();
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
