package com.revolut.money.rest.request;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

public class TransferMoneyRequestUnitTest implements SumValidationUnitTest, AccountIdValidationUnitTest {
    private AccountIdValidator<TransferMoneyRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, TransferMoneyRequest> buildRequest(Integer accountId) {
            return Map.of(
                    "fromAccountId", TransferMoneyRequest.builder().fromAccountId(accountId).build(),
                    "toAccountId", TransferMoneyRequest.builder().toAccountId(accountId).build()
            );
        }
    };

    private SumValidator<TransferMoneyRequest> sumValidator = new SumValidator<>() {
        @Override
        protected TransferMoneyRequest buildRequest(BigDecimal sum) {
            return TransferMoneyRequest.builder().sum(sum).build();
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