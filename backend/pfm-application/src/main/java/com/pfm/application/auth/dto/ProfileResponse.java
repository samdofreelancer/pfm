package com.pfm.application.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProfileResponse {
    String id;
    String email;
    String fullName;
    String avatarUrl;
    boolean emailVerified;
}