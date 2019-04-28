package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.service.AccountService;

import java.math.BigDecimal;

public class GetBalanceRequestHandler {
    private final AccountService accountService;

    @Inject
    public GetBalanceRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public BigDecimal handle(int accountId) {
        return accountService.getBalance(accountId);
    }
}