package com.revolut.money.rest.request;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

public class WithdrawRequestUnitTest implements AccountIdValidationUnitTest, SumValidationUnitTest {
    private AccountIdValidator<WithdrawRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, WithdrawRequest> buildRequest(Integer accountId) {
            return Map.of("accountId", WithdrawRequest.builder().accountId(accountId).build());
        }
    };

    private SumValidator<WithdrawRequest> sumValidator = new SumValidator<>() {
        @Override
        protected WithdrawRequest buildRequest(BigDecimal sum) {
            return WithdrawRequest.builder().sum(sum).build();
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