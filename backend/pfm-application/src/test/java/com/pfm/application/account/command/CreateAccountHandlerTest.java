package com.pfm.application.account.command;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountHandlerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CreateAccountHandler handler;

    @Test
    void handle_ShouldCreateAccountForCurrentUser() {
        CreateAccountCommand command = new CreateAccountCommand(
                AccountType.CASH,
                "Wallet",
                "Daily cash",
                new BigDecimal("100.00"),
                "vnd"
        );
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountId accountId = handler.handle(command);

        assertNotNull(accountId);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        Account saved = accountCaptor.getValue();
        assertEquals("user-1", saved.getUserId().getValue());
        assertEquals("Wallet", saved.getName());
        assertEquals(new BigDecimal("100.00"), saved.getBalance().getAmount());
        assertEquals("VND", saved.getCurrency());
    }

    @Test
    void handle_ShouldUseDefaultBalanceAndCurrency_WhenCommandOmitsThem() {
        CreateAccountCommand command = new CreateAccountCommand(AccountType.CASH, "Wallet", null, null, null);
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        handler.handle(command);

        verify(accountRepository).save(argThat(account ->
                BigDecimal.ZERO.compareTo(account.getBalance().getAmount()) == 0
                        && "VND".equals(account.getCurrency())
        ));
    }
}
