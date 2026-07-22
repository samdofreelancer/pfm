package com.pfm.bootstrap.integration;

import com.pfm.application.account.command.CreateAccountHandler;
import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.application.auth.handler.PasswordHasher;
import com.pfm.application.auth.handler.TokenService;
import com.pfm.domain.account.repository.AccountRepository;
import com.pfm.domain.auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextIntegrationTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CreateAccountHandler createAccountHandler;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private TokenService tokenService;

    @Test
    void context_ShouldWireCleanArchitecturePortsAndAdapters() {
        assertNotNull(authRepository);
        assertNotNull(accountRepository);
        assertNotNull(createAccountHandler);
        assertNotNull(currentUserProvider);
        assertNotNull(passwordHasher);
        assertNotNull(tokenService);
    }
}
