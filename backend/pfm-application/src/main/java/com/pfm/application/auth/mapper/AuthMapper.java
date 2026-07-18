package com.pfm.application.auth.mapper;

import com.pfm.application.auth.dto.AuthResponse;
import com.pfm.domain.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "user.id", source = "user.id.value")
    @Mapping(target = "user.email", source = "user.email.value")
    @Mapping(target = "user.fullName", source = "user.fullName")
    @Mapping(target = "user.avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "expiresIn", ignore = true)
    AuthResponse toAuthResponse(User user);

    default AuthResponse toAuthResponseWithTokens(User user, String accessToken, String refreshToken, Long expiresIn) {
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