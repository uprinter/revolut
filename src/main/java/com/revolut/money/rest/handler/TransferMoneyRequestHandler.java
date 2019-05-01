package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.TransferMoneyRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

import java.math.BigDecimal;
import java.util.List;

public class TransferMoneyRequestHandler extends RequestHandler<TransferMoneyRequest, List<Account>> {
    private final AccountService accountService;

    @Inject
    public TransferMoneyRequestHandler(AccountService accountService, RequestValidator<TransferMoneyRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    protected TransferMoneyRequest buildRequestObject(Request request) {
        return new Gson().fromJson(request.body(), TransferMoneyRequest.class);
    }

    @Override
    protected List<Account> handle(TransferMoneyRequest transferMoneyRequest) {
        Integer fromAccountId = transferMoneyRequest.getFromAccountId();
        Integer toAccountId = transferMoneyRequest.getToAccountId();
        BigDecimal sum = transferMoneyRequest.getSum();

        return accountService.transferMoney(fromAccountId, toAccountId, sum);
    }
}