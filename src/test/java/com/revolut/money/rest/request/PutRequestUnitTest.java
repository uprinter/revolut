package com.revolut.money.rest.request;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

public class PutRequestUnitTest implements AccountIdValidationUnitTest, SumValidationUnitTest {
    private AccountIdValidator<PutRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, PutRequest> buildRequest(int accountId) {
            return Map.of("accountId", PutRequest.builder().accountId(accountId).build());
        }
    };

    private SumValidator<PutRequest> sumValidator = new SumValidator<>() {
        @Override
        protected PutRequest buildRequest(BigDecimal sum) {
            return PutRequest.builder().sum(sum).build();
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