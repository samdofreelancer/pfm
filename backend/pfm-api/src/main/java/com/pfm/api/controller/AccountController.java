package com.pfm.api.controller;

import com.pfm.api.dto.request.CreateAccountRequest;
import com.pfm.application.account.command.CreateAccountCommand;
import com.pfm.application.account.command.CreateAccountHandler;
import com.pfm.application.account.command.DeleteAccountCommand;
import com.pfm.application.account.command.DeleteAccountHandler;
import com.pfm.application.account.mapper.AccountMapper;
import com.pfm.application.account.query.GetAccountsHandler;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.repository.AccountRepository;
import com.pfm.domain.auth.repository.AuthRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final CreateAccountHandler createAccountHandler;
    private final DeleteAccountHandler deleteAccountHandler;
    private final GetAccountsHandler getAccountsHandler;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final AuthRepository authRepository;

    @PostMapping
    public ResponseEntity<Void> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = new CreateAccountCommand(
            request.getUserId(),
            request.getType(),
            request.getName(),
            request.getDescription(),
            request.getInitialBalance(),
            request.getCurrency()
        );

        createAccountHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<AccountMapper.AccountResponse>> getAccounts(@RequestParam String userId) {
        // TODO: Get userId from authenticated user instead of request param
        List<AccountMapper.AccountResponse> accounts = getAccountsHandler.handle(new com.pfm.application.account.query.GetAccountsQuery(userId))
            .stream()
            .map(accountMapper::toResponse)
            .toList();
        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String id, @RequestParam String userId) {
        deleteAccountHandler.handle(new DeleteAccountCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupAccountsByEmail(@RequestParam String email) {
        var authUser = authRepository.findByEmail(email);
        if (authUser.isPresent()) {
            var accounts = accountRepository.findByUserId(AccountId.from(authUser.get().getId().getValue().toString()));
            for (var account : accounts) {
                accountRepository.delete(account.getId());
            }
        }
        return ResponseEntity.noContent().build();
    }
}
