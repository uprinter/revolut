package com.revolut.money.rest.request;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

public class PutMoneyRequestUnitTest implements AccountIdValidationUnitTest, SumValidationUnitTest {
    private AccountIdValidator<PutMoneyRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, PutMoneyRequest> buildRequest(Integer accountId) {
            return Map.of("accountId", PutMoneyRequest.builder().accountId(accountId).build());
        }
    };

    private SumValidator<PutMoneyRequest> sumValidator = new SumValidator<>() {
        @Override
        protected PutMoneyRequest buildRequest(BigDecimal sum) {
            return PutMoneyRequest.builder().sum(sum).build();
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