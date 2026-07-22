package com.pfm.infrastructure.security.service;

import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringSecurityCurrentUserProviderTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private SpringSecurityCurrentUserProvider provider;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentUserEmail_ShouldReturnAuthenticatedPrincipalName() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@example.com", null)
        );

        assertEquals("john@example.com", provider.currentUserEmail());
    }

    @Test
    void currentUserEmail_ShouldThrowUnauthenticated_WhenNoAuthenticationExists() {
        BusinessException exception = assertThrows(BusinessException.class, provider::currentUserEmail);

        assertEquals("UNAUTHENTICATED", exception.getCode());
        assertEquals(401, exception.getStatus());
    }

    @Test
    void currentUserId_ShouldResolveUserIdFromAuthenticatedEmail() {
        AuthUser authUser = AuthUser.create("john@example.com", "encoded");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@example.com", null)
        );
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(authUser));

        assertEquals(authUser.getId().getValue().toString(), provider.currentUserId());
    }

    @Test
    void currentUserId_ShouldThrowNotFound_WhenAuthenticatedUserDoesNotExist() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john@example.com", null)
        );
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, provider::currentUserId);

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
    }
}
