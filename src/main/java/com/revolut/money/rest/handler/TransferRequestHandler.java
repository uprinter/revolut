package com.revolut.money.rest.handler;

import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.service.AccountService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class TransferRequestHandler {
    private final AccountService accountService;

    // @todo add validation
    public void handle(TransferRequest transferRequest) {
        int fromAccountId = transferRequest.getFromAccountId();
        int toAccountId = transferRequest.getToAccountId();
        BigDecimal sum = transferRequest.getSum();

        accountService.transferMoney(fromAccountId, toAccountId, sum);
    }
}