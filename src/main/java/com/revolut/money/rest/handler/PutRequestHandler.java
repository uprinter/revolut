package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

import java.math.BigDecimal;

public class PutRequestHandler extends RequestHandler<PutRequest, Account> {
    private final AccountService accountService;

    @Inject
    public PutRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected PutRequest buildRequestObject(Request request) {
        return new Gson().fromJson(request.body(), PutRequest.class);
    }

    @Override
    protected Account handle(PutRequest putRequest) {
        int accountId = putRequest.getAccountId();
        BigDecimal sum = putRequest.getSum();

        return accountService.putMoney(accountId, sum);
    }
}