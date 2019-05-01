package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.WithdrawRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

import java.math.BigDecimal;

public class WithdrawRequestHandler extends RequestHandler<WithdrawRequest, Account> {
    private final AccountService accountService;

    @Inject
    public WithdrawRequestHandler(AccountService accountService, RequestValidator<WithdrawRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    protected WithdrawRequest buildRequestObject(Request request) {
        return new Gson().fromJson(request.body(), WithdrawRequest.class);
    }

    @Override
    protected Account handle(WithdrawRequest withdrawRequest) {
        Integer accountId = withdrawRequest.getAccountId();
        BigDecimal sum = withdrawRequest.getSum();

        return accountService.withdrawMoney(accountId, sum);
    }
}