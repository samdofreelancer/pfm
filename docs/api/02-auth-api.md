# 🔐 Authentication API

## Overview

This document describes authentication endpoints and how clients should obtain access tokens to use the PFM API.

## Endpoints

### Register User
- `POST /api/v1/auth/register`
- Request: `RegisterRequest`
- Response: `AuthResponse`

### Login
- `POST /api/v1/auth/login`
- Request: `LoginRequest`
- Response: `AuthResponse`

### Refresh Token
- `POST /api/v1/auth/refresh`
- Request: `RefreshTokenRequest`
- Response: `AuthResponse`

### Logout
- `POST /api/v1/auth/logout`
- Request: `LogoutRequest`
- Response: `204 No Content`

## Request / Response Shapes

### LoginRequest
- `email` (string, required)
- `password` (string, required)

### RegisterRequest
- `email` (string, required)
- `password` (string, required)
- `fullName` (string, optional)

### AuthResponse
- `accessToken` (string)
- `refreshToken` (string)
- `expiresIn` (integer)
- `tokenType` (string)

## Authentication Flow

1. User submits credentials to `/auth/login`
2. Server validates credentials and returns JWT token
3. Client uses `Authorization: Bearer {token}` for protected endpoints
4. When token expires, client requests refresh token

## Security Notes

- Passwords must be hashed before storage
- Refresh tokens should be stored securely
- Use HTTPS to protect credentials in transit
