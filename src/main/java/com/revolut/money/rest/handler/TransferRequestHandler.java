package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.service.AccountService;

import java.math.BigDecimal;

public class TransferRequestHandler {
    private final AccountService accountService;

    @Inject
    public TransferRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    // @todo add validation
    public void handle(TransferRequest transferRequest) {
        int fromAccountId = transferRequest.getFromAccountId();
        int toAccountId = transferRequest.getToAccountId();
        BigDecimal sum = transferRequest.getSum();

        accountService.transferMoney(fromAccountId, toAccountId, sum);
    }
}