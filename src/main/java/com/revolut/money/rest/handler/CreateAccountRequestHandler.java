package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.service.AccountService;

public class CreateAccountRequestHandler {
    private final AccountService accountService;

    @Inject
    public CreateAccountRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public JsonElement handle() {
        Account account = accountService.createAccount();
        return new Gson().toJsonTree(account);
    }
}