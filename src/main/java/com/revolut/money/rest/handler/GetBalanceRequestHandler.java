package com.revolut.money.rest.handler;

import com.revolut.money.service.AccountService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class GetBalanceRequestHandler {
    private final AccountService accountService;

    public BigDecimal handle(int accountId) {
        return accountService.getBalance(accountId);
    }
}