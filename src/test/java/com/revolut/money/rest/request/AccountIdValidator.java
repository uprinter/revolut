package com.revolut.money.rest.request;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class AccountIdValidator<T> {
    private Validator validator = buildDefaultValidatorFactory().getValidator();

    protected abstract Map<String, T> buildRequest(int accountId);

    void shouldBeValidIfAccountIdIsPositive() {
        Map<String, T> requests = buildRequest(1);

        for (String fieldName : requests.keySet()) {
            // given
            T request = requests.get(fieldName);

            // when
            Set<ConstraintViolation<T>> violations = validator.validate(request);

            // then
            assertThat(violations, not(hasItem(hasProperty(fieldName))));
        }
    }

    void shouldBeInvalidIfAccountIdIsZero() {
        Map<String, T> requests = buildRequest(0);

        for (String fieldName : requests.keySet()) {
            // given
            T request = requests.get(fieldName);

            // when
            Set<ConstraintViolation<T>> violations = validator.validate(request);

            // then
            assertThat(violations, hasItem(allOf(
                    hasProperty("propertyPath", hasToString(fieldName)),
                    hasProperty("invalidValue", is(0)),
                    hasProperty("message", is("Account ID should be positive"))
            )));
        }
    }

    void shouldBeInvalidIfAccountIdIsNegative() {
        Map<String, T> requests = buildRequest(-1);

        for (String fieldName : requests.keySet()) {
            // given
            T request = requests.get(fieldName);

            // when
            Set<ConstraintViolation<T>> violations = validator.validate(request);

            // then
            assertThat(violations, hasItem(allOf(
                    hasProperty("propertyPath", hasToString(fieldName)),
                    hasProperty("invalidValue", is(-1)),
                    hasProperty("message", is("Account ID should be positive"))
            )));
        }
    }
}