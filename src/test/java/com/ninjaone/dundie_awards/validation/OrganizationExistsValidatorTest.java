package com.ninjaone.dundie_awards.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ninjaone.dundie_awards.service.OrganizationService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrganizationExistsValidatorTest {

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ConstraintValidatorContext context;

    private OrganizationExistsValidator validator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validator = new OrganizationExistsValidator(organizationService);
    }

    @Test
    void shouldReturnTrueForNullOrganizationId() {
        boolean result = validator.isValid(null, context);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForExistingOrganizationId() {
        when(organizationService.exists(1L)).thenReturn(true);
        boolean result = validator.isValid(1L, context);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNonExistingOrganizationId() {
        when(organizationService.exists(999L)).thenReturn(false);
        boolean result = validator.isValid(999L, context);
        assertThat(result).isFalse();
    }
}
