package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.service.AccountService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class PutRequestHandler extends RequestHandler<PutRequest, Account> {
    private final AccountService accountService;

    @Inject
    public PutRequestHandler(AccountService accountService) {
        super(PutRequest.class);
        this.accountService = accountService;
    }

    @Override
    protected Optional<Account> handle(PutRequest putRequest, Map<String, String> params) {
        int accountId = putRequest.getAccountId();
        BigDecimal sum = putRequest.getSum();

        Account account = accountService.putMoney(accountId, sum);
        return Optional.of(account);
    }
}