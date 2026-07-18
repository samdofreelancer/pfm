package com.pfm.application.auth.mapper;

import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.domain.auth.model.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponseWithTokens(AuthUser authUser, String accessToken, String refreshToken, Long expiresIn) {
        AuthResponse.AuthResponseBuilder builder = AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(expiresIn);

        if (authUser != null) {
            builder.user(AuthResponse.UserInfo.builder()
                .id(authUser.getId().getValue())
                .email(authUser.getEmail().getValue())
                .fullName(null)
                .avatarUrl(null)
                .build());
        }

        return builder.build();
    }
}
