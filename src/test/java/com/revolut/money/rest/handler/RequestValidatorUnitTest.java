package com.revolut.money.rest.handler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RequestValidatorUnitTest {
    @Mock
    private Validator validator;

    @Mock(lenient = true)
    private ConstraintViolation<Object> constraintViolationOne;

    @Mock(lenient = true)
    private ConstraintViolation<Object> constraintViolationTwo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private RequestValidator<Object> requestValidator;

    @Test
    public void shouldDoNothingIfRequestIsValid() {
        // given
        given(validator.validate(any())).willReturn(Set.of());

        // when
        requestValidator.validate(new Object());
    }

    @Test
    public void shouldThrowValidationExceptionIfRequestIsInvalid() {
        // given
        given(constraintViolationOne.getMessage()).willReturn("messageOne");
        given(constraintViolationTwo.getMessage()).willReturn("messageTwo");

        Set<ConstraintViolation<Object>> constraintViolations = Set.of(constraintViolationOne, constraintViolationTwo);

        given(validator.validate(any())).willReturn(constraintViolations);

        // expect
        expectedException.expect(allOf(
                isA(ValidationException.class),
                hasProperty("message", startsWith("message"))
        ));

        // when
        requestValidator.validate(new Object());
    }
}