package com.revolut.money.rest.request;

public interface SumValidationUnitTest {
    void shouldBeValidIfSumIsPositive();
    void shouldBeInvalidIfSumIsZero();
    void shouldBeInvalidIfSumIsNegative();
}