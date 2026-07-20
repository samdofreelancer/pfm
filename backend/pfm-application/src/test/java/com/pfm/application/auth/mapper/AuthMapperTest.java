package com.pfm.application.auth.mapper;

import com.pfm.domain.auth.model.AuthUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthMapperTest {

    private final AuthMapper mapper = new AuthMapper();

    @Test
    void toAuthResponseWithTokens_ShouldIncludeTokenFieldsAndUserInfo() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded", "John Doe");

        var response = mapper.toAuthResponseWithTokens(authUser, "access", "refresh", 900000L);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals(900000L, response.getExpiresIn());
        assertNotNull(response.getUser());
        assertEquals(authUser.getId().getValue().toString(), response.getUser().getId());
        assertEquals("john@example.com", response.getUser().getEmail());
        assertEquals("John Doe", response.getUser().getFullName());
    }

    @Test
    void toAuthResponseWithTokens_ShouldAllowNullUser() {
        var response = mapper.toAuthResponseWithTokens(null, "access", "refresh", 900000L);

        assertEquals("access", response.getAccessToken());
        assertNull(response.getUser());
    }

    @Test
    void toProfileResponse_ShouldMapProfileFields() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded", "John Doe");

        var response = mapper.toProfileResponse(authUser);

        assertEquals(authUser.getId().getValue().toString(), response.getId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getFullName());
        assertFalse(response.isEmailVerified());
    }
}
