package com.pfm.application.auth.handler;

import com.pfm.domain.user.model.User;

public interface TokenService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    long getAccessTokenExpirationMs();
    long getRefreshTokenExpirationMs();
    String getEmailFromToken(String token);
    boolean validateToken(String token);
}