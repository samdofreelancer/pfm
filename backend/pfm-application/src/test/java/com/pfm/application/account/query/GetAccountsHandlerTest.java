package com.pfm.application.account.query;

import com.pfm.application.auth.handler.CurrentUserProvider;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountsHandlerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private GetAccountsHandler handler;

    @Test
    void handle_ShouldQueryActiveAccountsForCurrentUser() {
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(accountRepository.findActiveByUserId(AccountOwnerId.from("user-1"))).thenReturn(List.of());

        var result = handler.handle(new GetAccountsQuery());

        assertEquals(List.of(), result);
        verify(accountRepository).findActiveByUserId(argThat(ownerId -> "user-1".equals(ownerId.getValue())));
    }
}
