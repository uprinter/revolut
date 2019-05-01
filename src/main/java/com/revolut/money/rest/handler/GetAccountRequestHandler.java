package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.GetRequest;
import com.revolut.money.service.AccountService;
import spark.Request;

public class GetAccountRequestHandler extends RequestHandler<GetRequest, Account> {
    private final AccountService accountService;

    @Inject
    public GetAccountRequestHandler(AccountService accountService, RequestValidator<GetRequest> requestValidator) {
        super(requestValidator);
        this.accountService = accountService;
    }

    @Override
    // @todo replace int to Integer
    protected GetRequest buildRequestObject(Request request) {
        String stringAccountId = request.params(":id");
        int accountId = Integer.valueOf(stringAccountId);
        return GetRequest.builder().accountId(accountId).build();
    }

    @Override
    protected Account handle(GetRequest getRequest) {
        int accountId = getRequest.getAccountId();
        return accountService.findAccount(accountId);
    }
}