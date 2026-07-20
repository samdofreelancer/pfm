package com.pfm.application.account.command;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.model.Money;
import com.pfm.domain.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountHandlerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private DeleteAccountHandler handler;

    @Test
    void handle_ShouldSoftDeleteAccount_WhenCurrentUserOwnsIt() {
        Account account = accountForOwner("user-1");
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(accountRepository.findById(AccountId.from("account-1"))).thenReturn(Optional.of(account));

        handler.handle(new DeleteAccountCommand("account-1"));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertFalse(accountCaptor.getValue().isActive());
        assertNotNull(accountCaptor.getValue().getDeletedAt());
    }

    @Test
    void handle_ShouldThrowNotFound_WhenAccountDoesNotExist() {
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(accountRepository.findById(AccountId.from("missing"))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> handler.handle(new DeleteAccountCommand("missing")));

        assertEquals("ACCOUNT_NOT_FOUND", exception.getCode());
        assertEquals(404, exception.getStatus());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void handle_ShouldThrowForbidden_WhenAccountBelongsToAnotherUser() {
        Account account = accountForOwner("other-user");
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(accountRepository.findById(AccountId.from("account-1"))).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> handler.handle(new DeleteAccountCommand("account-1")));

        assertEquals("ACCOUNT_FORBIDDEN", exception.getCode());
        assertEquals(403, exception.getStatus());
        verify(accountRepository, never()).save(any());
    }

    private Account accountForOwner(String ownerId) {
        return Account.restore(
                AccountId.from("account-1"),
                AccountOwnerId.from(ownerId),
                AccountType.CASH,
                "Wallet",
                null,
                Money.of(new BigDecimal("100.00"), "VND"),
                true,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                null
        );
    }
}
