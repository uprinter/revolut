package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.PutMoneyRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

import java.math.BigDecimal;

public class PutMoneyRequestHandler extends RequestHandler<PutMoneyRequest, Account> {
    private final AccountService accountService;

    @Inject
    public PutMoneyRequestHandler(AccountService accountService, RequestValidator<PutMoneyRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    protected PutMoneyRequest buildRequestObject(Request request) {
        return new Gson().fromJson(request.body(), PutMoneyRequest.class);
    }

    @Override
    protected Account handle(PutMoneyRequest putMoneyRequest) {
        Integer accountId = putMoneyRequest.getAccountId();
        BigDecimal sum = putMoneyRequest.getSum();

        return accountService.putMoney(accountId, sum);
    }
}