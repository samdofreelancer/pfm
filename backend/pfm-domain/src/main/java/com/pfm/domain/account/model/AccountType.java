package com.pfm.domain.account.model;

import lombok.Getter;

@Getter
public enum AccountType {
    CASH("Tiền mặt"),
    BANK_ACCOUNT("Tài khoản ngân hàng"),
    CREDIT_CARD("Thẻ tín dụng"),
    DEBIT_CARD("Thẻ ghi nợ"),
    E_WALLET("Ví điện tử");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }
}