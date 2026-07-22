package com.pfm.api.controller;

import com.pfm.api.dto.request.CreateAccountRequest;
import com.pfm.application.account.command.CreateAccountCommand;
import com.pfm.application.account.command.CreateAccountHandler;
import com.pfm.application.account.command.DeleteAccountCommand;
import com.pfm.application.account.command.DeleteAccountHandler;
import com.pfm.application.account.mapper.AccountMapper;
import com.pfm.application.account.query.GetAccountsHandler;
import com.pfm.application.account.query.GetAccountsQuery;
import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.model.AccountType;
import com.pfm.domain.account.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private CreateAccountHandler createAccountHandler;

    @Mock
    private DeleteAccountHandler deleteAccountHandler;

    @Mock
    private GetAccountsHandler getAccountsHandler;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountController controller;

    @Test
    void createAccount_ShouldMapRequestWithoutClientUserId() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setType(AccountType.CASH);
        request.setName("Wallet");
        request.setDescription("Daily cash");
        request.setInitialBalance(new BigDecimal("100.00"));
        request.setCurrency("VND");
        when(createAccountHandler.handle(any(CreateAccountCommand.class))).thenReturn(AccountId.from("account-1"));

        var response = controller.createAccount(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ArgumentCaptor<CreateAccountCommand> commandCaptor = ArgumentCaptor.forClass(CreateAccountCommand.class);
        verify(createAccountHandler).handle(commandCaptor.capture());
        assertEquals(AccountType.CASH, commandCaptor.getValue().type());
        assertEquals("Wallet", commandCaptor.getValue().name());
        assertEquals(new BigDecimal("100.00"), commandCaptor.getValue().initialBalance());
        assertEquals("VND", commandCaptor.getValue().currency());
    }

    @Test
    void getAccounts_ShouldUseQueryWithoutClientUserId() {
        Account account = Account.restore(
                AccountId.from("account-1"),
                AccountOwnerId.from("user-1"),
                AccountType.CASH,
                "Wallet",
                null,
                Money.of(new BigDecimal("100.00"), "VND"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        AccountMapper.AccountResponse mapped = new AccountMapper.AccountResponse(
                "account-1",
                "user-1",
                AccountType.CASH,
                "Wallet",
                null,
                new BigDecimal("100.00"),
                "VND",
                true,
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
        when(getAccountsHandler.handle(any(GetAccountsQuery.class))).thenReturn(List.of(account));
        when(accountMapper.toResponse(account)).thenReturn(mapped);

        var response = controller.getAccounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(mapped), response.getBody());
        verify(getAccountsHandler).handle(new GetAccountsQuery());
    }

    @Test
    void deleteAccount_ShouldMapPathIdOnly() {
        var response = controller.deleteAccount("account-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteAccountHandler).handle(new DeleteAccountCommand("account-1"));
    }
}
