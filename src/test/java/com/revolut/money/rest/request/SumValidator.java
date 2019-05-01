package com.revolut.money.rest.request;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class SumValidator<T> {
    private Validator validator = buildDefaultValidatorFactory().getValidator();

    protected abstract T buildRequest(BigDecimal sum);

    void shouldBeValidatedSuccessfullyIfSumIsPositive() {
        // given
        T request = buildRequest(BigDecimal.ONE);

        // when
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        // then
        assertThat(violations, not(hasItem(hasProperty("sum"))));
    }

    void shouldFailValidationIfSumIsZero() {
        // given
        T request = buildRequest(BigDecimal.ZERO);

        // when
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        // then
        assertThat(violations, hasItem(allOf(
                hasProperty("invalidValue", is(BigDecimal.ZERO)),
                hasProperty("message", is("Sum should be positive"))
        )));
    }

    void shouldFailValidationIfSumIsNegative() {
        // given
        BigDecimal minusOne = BigDecimal.ZERO.min(BigDecimal.ONE);
        T request = buildRequest(minusOne);

        // when
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        // then
        assertThat(violations, hasItem(allOf(
                hasProperty("invalidValue", is(minusOne)),
                hasProperty("message", is("Sum should be positive"))
        )));
    }
}