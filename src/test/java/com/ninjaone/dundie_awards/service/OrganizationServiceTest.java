package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Organization createOrganization(Long id) {
        Organization organization = new Organization();
        organization.setId(id);
        return organization;
    }

    @Nested
    class EnsureValidOrganizationTests {

        @Test
        void shouldPassWhenOrganizationExists() {
            Long validId = 1L;
            given(organizationRepository.existsById(validId)).willReturn(true);

            organizationService.ensureValidOrganization(validId);

            verify(organizationRepository).existsById(validId);
        }

        @Test
        void shouldThrowInvalidIdExceptionForNullId() {
            IllegalArgumentException exception = catchThrowableOfType(
                () -> organizationService.ensureValidOrganization(null),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("The provided organization ID cannot be null.");
        }

        @Test
        void shouldThrowOrganizationNotValidExceptionForNonExistingOrganization() {
            Long invalidId = 999L;
            given(organizationRepository.existsById(invalidId)).willReturn(false);

            IllegalArgumentException exception = catchThrowableOfType(
                () -> organizationService.ensureValidOrganization(invalidId),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }
    }

    @Nested
    class GetValidOrganizationTests {

        @Test
        void shouldReturnOrganizationWhenExists() {
            Long validId = 1L;
            Organization organization = createOrganization(validId);
            given(organizationRepository.findById(validId)).willReturn(Optional.of(organization));

            Organization result = organizationService.getValidOrganization(validId);

            assertThat(result).isEqualTo(organization);
            verify(organizationRepository).findById(validId);
        }

        @Test
        void shouldThrowInvalidIdExceptionForNullId() {
            IllegalArgumentException exception = catchThrowableOfType(
                () -> organizationService.getValidOrganization(null),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("The provided organization ID cannot be null.");
        }

        @Test
        void shouldThrowOrganizationNotValidExceptionForNonExistingOrganization() {
            Long invalidId = 999L;
            given(organizationRepository.findById(invalidId)).willReturn(Optional.empty());

            IllegalArgumentException exception = catchThrowableOfType(
                () -> organizationService.getValidOrganization(invalidId),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }
    }
}
