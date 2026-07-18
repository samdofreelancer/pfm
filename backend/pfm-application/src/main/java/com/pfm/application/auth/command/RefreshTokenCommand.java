package com.pfm.application.auth.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RefreshTokenCommand {

    @NotBlank(message = "Refresh token is required")
    String refreshToken;
}
