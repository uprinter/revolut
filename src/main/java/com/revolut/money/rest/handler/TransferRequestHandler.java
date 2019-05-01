package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

import java.math.BigDecimal;
import java.util.List;

public class TransferRequestHandler extends RequestHandler<TransferRequest, List<Account>> {
    private final AccountService accountService;

    @Inject
    public TransferRequestHandler(AccountService accountService, RequestValidator<TransferRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    protected TransferRequest buildRequestObject(Request request) {
        return new Gson().fromJson(request.body(), TransferRequest.class);
    }

    @Override
    protected List<Account> handle(TransferRequest transferRequest) {
        Integer fromAccountId = transferRequest.getFromAccountId();
        Integer toAccountId = transferRequest.getToAccountId();
        BigDecimal sum = transferRequest.getSum();

        return accountService.transferMoney(fromAccountId, toAccountId, sum);
    }
}