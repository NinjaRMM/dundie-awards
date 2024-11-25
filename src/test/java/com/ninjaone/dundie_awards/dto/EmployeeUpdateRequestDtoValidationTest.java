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
class EmployeeUpdateRequestDtoValidationTest {

	@Autowired
	private LocalValidatorFactoryBean validator;

	@BeforeEach
	void setUp() {
		LocaleContextHolder.setLocale(Locale.US);
	}

	@Test
	void shouldFailValidationForInvalidOrganizationId() {
		EmployeeUpdateRequestDto invalidDto = EmployeeUpdateRequestDto.builder()
				.firstName("Ryan")
				.lastName("Howard")
				.dundieAwards(5)
				.organizationId(999L)
				.build();

		var constraintViolations = validator.validate(invalidDto);

		assertThat(constraintViolations).isNotEmpty();
		assertThat(constraintViolations).anyMatch(v -> v.getPropertyPath().toString().equals("organizationId")
				&& v.getMessage().equals("The organization ID is not valid."));
	}

	@Test
	void shouldFailValidationForMissingFirstName() {
		EmployeeUpdateRequestDto invalidDto = EmployeeUpdateRequestDto.builder()
				.lastName("Howard")
				.dundieAwards(5)
				.organizationId(1L)
				.build();

		var constraintViolations = validator.validate(invalidDto);

		assertThat(constraintViolations).isNotEmpty();
		assertThat(constraintViolations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName")
				&& v.getMessage().equals("must not be blank"));
	}

	@Test
	void shouldFailValidationForNegativeDundieAwards() {
		EmployeeUpdateRequestDto invalidDto = EmployeeUpdateRequestDto.builder()
				.firstName("Ryan")
				.lastName("Howard")
				.dundieAwards(-1)
				.organizationId(1L)
				.build();

		var constraintViolations = validator.validate(invalidDto);

		assertThat(constraintViolations).isNotEmpty();
		assertThat(constraintViolations).anyMatch(v -> v.getPropertyPath().toString().equals("dundieAwards")
				&& v.getMessage().equals("must be greater than or equal to 0"));
	}

	@Test
	void shouldPassValidationForValidData() {
		EmployeeUpdateRequestDto validDto = EmployeeUpdateRequestDto.builder()
				.firstName("Ryan")
				.lastName("Howard")
				.dundieAwards(5)
				.organizationId(1L)
				.build();

		var constraintViolations = validator.validate(validDto);

		assertThat(constraintViolations).isEmpty();
	}
}
