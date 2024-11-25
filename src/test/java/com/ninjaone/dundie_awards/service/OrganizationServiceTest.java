package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.service.impl.OrganizationServiceImpl;

class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class GetValidOrganizationTests {

        @Test
        void shouldReturnOrganizationWhenExists() {
            Long validId = 1L;
            Organization organization = Organization.builder().id(validId).build();
            given(organizationRepository.findById(validId)).willReturn(Optional.of(organization));

            Organization result = organizationService.getOrganization(validId);

            assertThat(result).isEqualTo(organization);
            verify(organizationRepository).findById(validId);
        }

        @Test
        void shouldReturnNullForNullId() {
            Organization organization = organizationService.getOrganization(null);

            assertThat(organization).isNull();
        }


        @Test
        void shouldThrowOrganizationNotFoundExceptionForNonExistingOrganization() {
            Long invalidId = 999L;
            given(organizationRepository.findById(invalidId)).willReturn(Optional.empty());

            NoSuchElementException exception = catchThrowableOfType(
                () -> organizationService.getOrganization(invalidId),
                NoSuchElementException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Organization with id: 999 not found");
        }
    }
}
