package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.EmptyRequest;
import com.revolut.money.service.AccountService;

import java.util.Map;
import java.util.Optional;

public class CreateAccountRequestHandler extends RequestHandler<EmptyRequest, Account> {
    private final AccountService accountService;

    @Inject
    public CreateAccountRequestHandler(AccountService accountService) {
        super(EmptyRequest.class);
        this.accountService = accountService;
    }

    @Override
    protected Optional<Account> handle(EmptyRequest emptyRequest, Map<String, String> params) {
        Account account = accountService.createAccount();
        return Optional.of(account);
    }
}