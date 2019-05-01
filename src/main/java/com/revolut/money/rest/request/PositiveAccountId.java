package com.revolut.money.rest.request;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Positive;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Positive(message = "Account ID should be positive")
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
public @interface PositiveAccountId {
    String message() default "";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};
}