package com.revolut.money.rest.handler;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.service.AccountService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class TransferRequestHandler extends RequestHandler<TransferRequest, Account> {
    private final AccountService accountService;

    @Inject
    public TransferRequestHandler(AccountService accountService) {
        super(TransferRequest.class);
        this.accountService = accountService;
    }

    // @todo add validation
    @Override
    protected Optional<Account> handle(TransferRequest transferRequest, Map<String, String> params) {
        int fromAccountId = transferRequest.getFromAccountId();
        int toAccountId = transferRequest.getToAccountId();
        BigDecimal sum = transferRequest.getSum();

        accountService.transferMoney(fromAccountId, toAccountId, sum);
        // @todo return list of updated accounts
        return Optional.empty();
    }
}