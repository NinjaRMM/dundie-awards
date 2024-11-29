package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.dto.OrganizationDto;
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
    class ExistsTests {

        @Test
        void shouldReturnTrueWhenOrganizationExists() {
            Long validId = 1L;
            given(organizationRepository.existsById(validId)).willReturn(true);

            boolean result = organizationService.exists(validId);

            assertThat(result).isTrue();
            verify(organizationRepository).existsById(validId);
        }

        @Test
        void shouldReturnFalseWhenOrganizationDoesNotExist() {
            Long invalidId = 999L;
            given(organizationRepository.existsById(invalidId)).willReturn(false);

            boolean result = organizationService.exists(invalidId);

            assertThat(result).isFalse();
            verify(organizationRepository).existsById(invalidId);
        }

        @Test
        void shouldReturnFalseForNullOrganizationId() {
            boolean result = organizationService.exists(null);

            assertThat(result).isFalse();
        }
    }


    @Nested
    class GetValidOrganizationTests {

        @Test
        void shouldReturnOrganizationWhenExists() {
            Long validId = 1L;
            Organization organization = Organization.builder().id(validId).blocked(false).build();
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
    
    @Nested
    class IsBlockedTests {

        @Test
        void shouldReturnTrueWhenOrganizationIsBlocked() {
            Long blockedId = 1L;
            Organization blockedOrganization = Organization.builder().id(blockedId).blocked(true).build();
            given(organizationRepository.findById(blockedId)).willReturn(Optional.of(blockedOrganization));

            boolean result = organizationService.isBlocked(blockedId);

            assertThat(result).isTrue();
            verify(organizationRepository).findById(blockedId);
        }

        @Test
        void shouldReturnFalseWhenOrganizationIsNotBlocked() {
            Long unblockedId = 2L;
            Organization unblockedOrganization = Organization.builder().id(unblockedId).blocked(false).build();
            given(organizationRepository.findById(unblockedId)).willReturn(Optional.of(unblockedOrganization));

            boolean result = organizationService.isBlocked(unblockedId);

            assertThat(result).isFalse();
            verify(organizationRepository).findById(unblockedId);
        }

        @Test
        void shouldReturnFalseForNullOrganizationId() {
            boolean result = organizationService.isBlocked(null);

            assertThat(result).isFalse();
        }

        @Test
        void shouldThrowOrganizationNotFoundExceptionForNonExistingOrganization() {
            Long invalidId = 999L;
            given(organizationRepository.findById(invalidId)).willReturn(Optional.empty());

            NoSuchElementException exception = catchThrowableOfType(
                () -> organizationService.isBlocked(invalidId),
                NoSuchElementException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Organization with id: 999 not found");
        }
    }
    
    @Nested
    class BlockTests {

        @Test
        void shouldBlockOrganizationSuccessfully() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 1L;
            Organization existingOrganization = Organization.builder()
                .id(organizationId)
                .blocked(false)
                .build();
            Organization blockedOrganization = existingOrganization.toBuilder()
                .blocked(true)
                .blockedBy(uuid.toString())
                .build();

            given(organizationRepository.findById(organizationId)).willReturn(Optional.of(existingOrganization));
            given(organizationRepository.save(blockedOrganization)).willReturn(blockedOrganization);

            Organization result = organizationService.block(uuid, organizationId);

            assertThat(result).isEqualTo(blockedOrganization);
            assertThat(result.isBlocked()).isTrue();
            assertThat(result.getBlockedBy()).isEqualTo(uuid.toString());
            verify(organizationRepository).findById(organizationId);
            verify(organizationRepository).save(blockedOrganization);
        }

        @Test
        void shouldThrowExceptionWhenOrganizationDoesNotExist() {
            UUID uuid = UUID.randomUUID();
            Long invalidOrganizationId = 999L;

            given(organizationRepository.findById(invalidOrganizationId)).willReturn(Optional.empty());

            NoSuchElementException exception = catchThrowableOfType(
                () -> organizationService.block(uuid, invalidOrganizationId),
                NoSuchElementException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Organization with id: 999 not found");
            verify(organizationRepository).findById(invalidOrganizationId);
        }
    }
    
    @Nested
    class GetAllOrganizationsTests {

        @Test
        void shouldReturnAllOrganizations() {
            Organization org1 = Organization.builder().id(1L).name("Tom").blocked(false).blockedBy(null).build();
            Organization org2 = Organization.builder().id(2L).name("Frajola").blocked(true).blockedBy(UUID.randomUUID().toString()).build();

            given(organizationRepository.findAll()).willReturn(List.of(org1, org2));

            List<OrganizationDto> result = organizationService.getAllOrganizations();

            assertThat(result).hasSize(2);
            assertThat(result).anyMatch(dto -> dto.id().equals(org1.getId()) && dto.name().equals(org1.getName()) && !dto.blocked());
            assertThat(result).anyMatch(dto -> dto.id().equals(org2.getId()) && dto.name().equals(org2.getName()) && dto.blocked());
            verify(organizationRepository).findAll();
        }

        @Test
        void shouldReturnEmptyListWhenNoOrganizationsExist() {
            given(organizationRepository.findAll()).willReturn(List.of());

            List<OrganizationDto> result = organizationService.getAllOrganizations();

            assertThat(result).isEmpty();
            verify(organizationRepository).findAll();
        }
    }
    
    @Nested
    class UnblockTests {

        @Test
        void shouldUnblockOrganizationSuccessfully() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 1L;
            Organization blockedOrganization = Organization.builder()
                .id(organizationId)
                .name("Acme Corp")
                .blocked(true)
                .blockedBy(uuid.toString())
                .build();
            Organization unblockedOrganization = blockedOrganization.toBuilder()
                .blocked(false)
                .blockedBy(null)
                .build();

            given(organizationRepository.save(unblockedOrganization)).willReturn(unblockedOrganization);

            Organization result = organizationService.unblock(uuid, blockedOrganization);

            assertThat(result).isEqualTo(unblockedOrganization);
            assertThat(result.isBlocked()).isFalse();
            assertThat(result.getBlockedBy()).isNull();
            verify(organizationRepository).save(unblockedOrganization);
        }

        @Test
        void shouldThrowExceptionWhenUnblockingNonExistingOrganization() {
            UUID uuid = UUID.randomUUID();
            Organization nonExistingOrganization = Organization.builder()
                .id(999L)
                .name("Non-existing")
                .blocked(true)
                .build();

            given(organizationRepository.save(nonExistingOrganization)).willThrow(new IllegalArgumentException("Organization not found"));

            IllegalArgumentException exception = catchThrowableOfType(
                () -> organizationService.unblock(uuid, nonExistingOrganization),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Organization not found");
            verify(organizationRepository).save(nonExistingOrganization);
        }
    }



}
