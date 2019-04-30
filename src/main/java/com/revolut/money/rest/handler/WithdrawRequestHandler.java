package com.revolut.money.rest.handler;

import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.WithdrawRequest;
import com.revolut.money.service.AccountService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class WithdrawRequestHandler extends RequestHandler<WithdrawRequest, Account> {
    private final AccountService accountService;

    public WithdrawRequestHandler(AccountService accountService) {
        super(WithdrawRequest.class);
        this.accountService = accountService;
    }

    @Override
    protected Optional<Account> handle(WithdrawRequest withdrawRequest, Map<String, String> params) {
        int accountId = withdrawRequest.getAccountId();
        BigDecimal sum = withdrawRequest.getSum();

        Account account = accountService.withdrawMoney(accountId, sum);
        return Optional.of(account);
    }
}