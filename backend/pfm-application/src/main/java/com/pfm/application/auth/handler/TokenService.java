package com.pfm.application.auth.handler;

import com.pfm.domain.auth.model.AuthUser;

public interface TokenService {
    String generateAccessToken(AuthUser authUser);
    String generateRefreshToken(AuthUser authUser);
    long getAccessTokenExpirationMs();
    long getRefreshTokenExpirationMs();
    String getEmailFromToken(String token);
    boolean validateToken(String token);
}
