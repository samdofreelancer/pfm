package com.pfm.application.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String accessToken;
    String refreshToken;
    Long expiresIn;
    UserInfo user;

    @Value
    @Builder
    public static class UserInfo {
        String id;
        String email;
        String fullName;
        String avatarUrl;
    }
}