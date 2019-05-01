package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.EmptyRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

public class CreateAccountRequestHandler extends RequestHandler<EmptyRequest, Account> {
    private final AccountService accountService;

    @Inject
    public CreateAccountRequestHandler(AccountService accountService, RequestValidator<EmptyRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    protected EmptyRequest buildRequestObject(Request request) {
        return EmptyRequest.builder().build();
    }

    @Override
    protected Account handle(EmptyRequest emptyRequest) {
        return accountService.createAccount();
    }
}