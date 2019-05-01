package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.WithdrawMoneyRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

import java.math.BigDecimal;

public class WithdrawMoneyRequestHandler extends RequestHandler<WithdrawMoneyRequest, Account> {
    private final AccountService accountService;

    @Inject
    public WithdrawMoneyRequestHandler(AccountService accountService, RequestValidator<WithdrawMoneyRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    protected WithdrawMoneyRequest buildRequestObject(Request request) {
        return new Gson().fromJson(request.body(), WithdrawMoneyRequest.class);
    }

    @Override
    protected Account handle(WithdrawMoneyRequest withdrawMoneyRequest) {
        Integer accountId = withdrawMoneyRequest.getAccountId();
        BigDecimal sum = withdrawMoneyRequest.getSum();

        return accountService.withdrawMoney(accountId, sum);
    }
}