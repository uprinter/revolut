package com.revolut.money.rest.request;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

public class TransferRequestUnitTest implements SumValidationUnitTest, AccountIdValidationUnitTest {
    private AccountIdValidator<TransferRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, TransferRequest> buildRequest(int accountId) {
            return Map.of(
                    "fromAccountId", TransferRequest.builder().fromAccountId(accountId).build(),
                    "toAccountId", TransferRequest.builder().toAccountId(accountId).build()
            );
        }
    };

    private SumValidator<TransferRequest> sumValidator = new SumValidator<>() {
        @Override
        protected TransferRequest buildRequest(BigDecimal sum) {
            return TransferRequest.builder().sum(sum).build();
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