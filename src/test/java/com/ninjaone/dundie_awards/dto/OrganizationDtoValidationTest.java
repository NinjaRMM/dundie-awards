package com.ninjaone.dundie_awards.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest
@ActiveProfiles("test")
class OrganizationDtoValidationTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    void shouldFailValidationForMissingName() {
        OrganizationDto invalidDto = OrganizationDto.builder()
                .id(1L)
                .name(null) // Nome ausente
                .blocked(true)
                .blockedBy(UUID.randomUUID().toString())
                .build();

        var constraintViolations = validator.validate(invalidDto);

        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolations).anyMatch(v -> v.getPropertyPath().toString().equals("name")
                && v.getMessage().equals("must not be blank"));
    }

    @Test
    void shouldFailValidationForNegativeId() {
        OrganizationDto invalidDto = OrganizationDto.builder()
                .id(-1L) // ID inválido
                .name("Tom")
                .blocked(false)
                .blockedBy(UUID.randomUUID().toString())
                .build();

        var constraintViolations = validator.validate(invalidDto);

        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolations).anyMatch(v -> v.getPropertyPath().toString().equals("id")
                && v.getMessage().equals("must be greater than or equal to 0"));
    }

    @Test
    void shouldPassValidationForValidData() {
        OrganizationDto validDto = OrganizationDto.builder()
                .id(1L)
                .name("Tom")
                .blocked(false)
                .blockedBy(UUID.randomUUID().toString())
                .build();

        var constraintViolations = validator.validate(validDto);

        assertThat(constraintViolations).isEmpty();
    }
}
