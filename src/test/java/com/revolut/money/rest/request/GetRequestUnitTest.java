package com.revolut.money.rest.request;

import org.junit.Test;

import java.util.Map;

public class GetRequestUnitTest implements AccountIdValidationUnitTest {
    private AccountIdValidator<GetRequest> accountIdValidator = new AccountIdValidator<>() {
        @Override
        protected Map<String, GetRequest> buildRequest(Integer accountId) {
            return Map.of("accountId", GetRequest.builder().accountId(accountId).build());
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
}