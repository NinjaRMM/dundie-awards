package com.ninjaone.dundie_awards.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BlockedOrganizationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BlockedOrganizationId {
    String message() default "The organization is blocked and cannot perform this operation.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
