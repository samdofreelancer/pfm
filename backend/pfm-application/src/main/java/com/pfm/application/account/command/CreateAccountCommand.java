package com.pfm.application.account.command;

import com.pfm.domain.account.model.AccountType;

import java.math.BigDecimal;

public record CreateAccountCommand(AccountType type, String name, String description,
                                   BigDecimal initialBalance, String currency) {
}
