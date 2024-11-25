package com.ninjaone.dundie_awards.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest
class EmployeeDtoValidationTest {

    @Autowired
    private LocalValidatorFactoryBean validator;
    
    @BeforeEach
	void setUp() {
		LocaleContextHolder.setLocale(Locale.US);
	}

    @Test
    void shouldFailValidationForInvalidOrganizationId() {
        EmployeeDto invalidDto = EmployeeDto.builder()
                .id(1L)
                .firstName("Ryan")
                .lastName("Howard")
                .dundieAwards(5)
                .organizationId(999L)
                .organizationName("Invalid Organization")
                .build();

        var constraintViolations = validator.validate(invalidDto);

        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolations).anyMatch(v -> v.getPropertyPath().toString().equals("organizationId")
                && v.getMessage().equals("The organization ID is not valid."));
    }
}
