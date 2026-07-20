package com.pfm.api.advice;

import com.pfm.common.exception.BusinessException;
import com.pfm.domain.shared.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessException_ShouldUseExceptionStatusAndCode() {
        HttpServletRequest request = request("/api/test");

        var response = handler.handleBusinessException(
                new BusinessException("ACCOUNT_NOT_FOUND", "Account not found", 404),
                request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("ACCOUNT_NOT_FOUND", response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleDomainException_ShouldMapEmailExistsToConflict() {
        HttpServletRequest request = request("/api/auth/register");

        var response = handler.handleDomainException(new DomainException("EMAIL_EXISTS", "Email already registered"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("EMAIL_EXISTS", response.getBody().getCode());
    }

    @Test
    void handleDomainException_ShouldMapUserDisabledToForbidden() {
        HttpServletRequest request = request("/api/auth/login");

        var response = handler.handleDomainException(new DomainException("USER_DISABLED", "User account is disabled"), request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("USER_DISABLED", response.getBody().getCode());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalError() {
        HttpServletRequest request = request("/api/test");

        var response = handler.handleGeneralException(new RuntimeException("boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().getCode());
    }

    private HttpServletRequest request(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }
}
