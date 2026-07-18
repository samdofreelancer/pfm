package com.pfm.infrastructure.security.service;

import com.pfm.application.auth.handler.TokenService;
import com.pfm.domain.user.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService implements TokenService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    public String generateAccessToken(User user) {
        return generateToken(user.getEmail().getValue(), jwtExpirationMs);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(user.getEmail().getValue(), refreshExpirationMs);
    }

    @Override
    public long getAccessTokenExpirationMs() {
        return jwtExpirationMs;
    }

    @Override
    public long getRefreshTokenExpirationMs() {
        return refreshExpirationMs;
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String generateToken(String email, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSigningKey() {
        // Prefer treating secret as base64/base64url if possible; otherwise derive a 64-byte key
        // by hashing the provided secret with SHA-512 to ensure adequate length for HS512.
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (io.jsonwebtoken.io.DecodingException ex) {
            try {
                byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecret);
                return Keys.hmacShaKeyFor(keyBytes);
            } catch (io.jsonwebtoken.io.DecodingException ex2) {
                // Fallback: derive 64-byte key using SHA-512 of the secret string
                try {
                    java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-512");
                    byte[] digest = md.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    return Keys.hmacShaKeyFor(digest);
                } catch (java.security.NoSuchAlgorithmException nae) {
                    // As a last resort, use UTF-8 bytes (may throw if too short)
                    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
                    return Keys.hmacShaKeyFor(keyBytes);
                }
            }
        }
    }
}