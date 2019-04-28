package com.revolut.money.rest.handler;

import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.service.AccountService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class PutRequestHandler {
    private final AccountService accountService;

    public void handle(PutRequest putRequest) {
        int accountId = putRequest.getAccountId();
        BigDecimal sum = putRequest.getSum();

        accountService.putMoney(accountId, sum);
    }
}