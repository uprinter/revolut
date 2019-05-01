package com.revolut.money.rest.handler;

import com.google.inject.Inject;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

class RequestValidator<T> {
    private final Validator validator;

    @Inject
    public RequestValidator(Validator validator) {
        this.validator = validator;
    }

    void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            ConstraintViolation anyViolation = getAnyViolation(violations);
            throw new ValidationException(anyViolation.getMessage());
        }
    }

    private ConstraintViolation getAnyViolation(Set<ConstraintViolation<T>> violations) {
        ConstraintViolation[] constraintViolations = new ConstraintViolation[violations.size()];
        violations.toArray(constraintViolations);
        return constraintViolations[0];
    }
}