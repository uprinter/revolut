package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.service.AccountService;

import java.math.BigDecimal;

public class PutRequestHandler {
    private final AccountService accountService;

    @Inject
    public PutRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public void handle(PutRequest putRequest) {
        int accountId = putRequest.getAccountId();
        BigDecimal sum = putRequest.getSum();

        accountService.putMoney(accountId, sum);
    }
}