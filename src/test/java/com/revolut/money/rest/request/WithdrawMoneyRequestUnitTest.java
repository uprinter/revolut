package com.revolut.money.rest.request;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

public class WithdrawMoneyRequestUnitTest implements AccountIdValidationUnitTest, SumValidationUnitTest {
    private AccountIdValidator<WithdrawMoneyRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, WithdrawMoneyRequest> buildRequest(Integer accountId) {
            return Map.of("accountId", WithdrawMoneyRequest.builder().accountId(accountId).build());
        }
    };

    private SumValidator<WithdrawMoneyRequest> sumValidator = new SumValidator<>() {
        @Override
        protected WithdrawMoneyRequest buildRequest(BigDecimal sum) {
            return WithdrawMoneyRequest.builder().sum(sum).build();
        }
    };

    @Override
    @Test
    public void shouldBeValidIfAccountIdIsPositive() {
        accountIdValidator.shouldBeValidIfAccountIdIsPositive();
    }

    @Override
    @Test
    public void shouldBeInvalidIfAccountIdIsZero() {
        accountIdValidator.shouldBeInvalidIfAccountIdIsZero();
    }

    @Override
    @Test
    public void shouldBeInvalidIfAccountIdIsNegative() {
        accountIdValidator.shouldBeInvalidIfAccountIdIsNegative();
    }

    @Override
    @Test
    public void shouldBeValidIfSumIsPositive() {
        sumValidator.shouldBeValidatedSuccessfullyIfSumIsPositive();
    }

    @Override
    @Test
    public void shouldBeInvalidIfSumIsZero() {
        sumValidator.shouldFailValidationIfSumIsZero();
    }

    @Override
    @Test
    public void shouldBeInvalidIfSumIsNegative() {
        sumValidator.shouldFailValidationIfSumIsNegative();
    }
}