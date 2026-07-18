package com.pfm.application.auth.mapper;

import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponseWithTokens(User user, String accessToken, String refreshToken, Long expiresIn) {
        AuthResponse.AuthResponseBuilder builder = AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(expiresIn);

        if (user != null) {
            builder.user(AuthResponse.UserInfo.builder()
                .id(user.getId().getValue().toString())
                .email(user.getEmail().getValue())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build());
        }

        return builder.build();
    }
}