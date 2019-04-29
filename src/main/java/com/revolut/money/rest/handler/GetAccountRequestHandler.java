package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.service.AccountService;

public class GetAccountRequestHandler {
    private final AccountService accountService;

    @Inject
    public GetAccountRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public JsonElement handle(int accountId) {
        Account account = accountService.findAccount(accountId);
        return new Gson().toJsonTree(account);
    }
}