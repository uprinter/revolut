package com.revolut.money.rest.request;

public interface AccountIdValidationUnitTest {
    void shouldBeValidIfAccountIdIsPositive();
    void shouldBeInvalidIfAccountIdIsZero();
    void shouldBeInvalidIfAccountIdIsNegative();
}