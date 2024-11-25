package com.ninjaone.dundie_awards.dto;

import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployee;
import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployeeUpdateRequestDto;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.service.OrganizationService;

class EmployeeUpdateRequestDtoTest {
	
	@Mock
    private OrganizationService organizationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldUpdateEmployeeFieldsCorrectly() {
        Organization mockOrganization = Organization.builder()
                .id(2L)
                .name("Squanchy")
                .build();

        given(organizationService.getValidOrganization(2L)).willReturn(mockOrganization);

        Employee existingEmployee = createEmployee("Original", "Employee", 5, 1L);

        EmployeeUpdateRequestDto updateRequest = createEmployeeUpdateRequestDto(
                "Updated", "Employee", 10, 2L
        );

        Employee updatedEmployee = updateRequest.toEntity(existingEmployee, organizationService);

        assertSoftly(softly -> {
            softly.assertThat(updatedEmployee.getId()).isEqualTo(existingEmployee.getId());
            softly.assertThat(updatedEmployee.getFirstName()).isEqualTo("Updated");
            softly.assertThat(updatedEmployee.getLastName()).isEqualTo("Employee");
            softly.assertThat(updatedEmployee.getDundieAwards()).isEqualTo(10);
            softly.assertThat(updatedEmployee.getOrganization()).isEqualTo(mockOrganization);
        });
    }

    @Test
    void shouldSetOrganizationToNullWhenRequested() {
        Employee existingEmployee = createEmployee("Original", "Employee", 5, 1L);

        EmployeeUpdateRequestDto updateRequest = createEmployeeUpdateRequestDto(
                "Updated", "Employee", 10, null
        );

        Employee updatedEmployee = updateRequest.toEntity(existingEmployee, organizationService);

        assertSoftly(softly -> {
            softly.assertThat(updatedEmployee.getId()).isEqualTo(existingEmployee.getId());
            softly.assertThat(updatedEmployee.getFirstName()).isEqualTo("Updated");
            softly.assertThat(updatedEmployee.getLastName()).isEqualTo("Employee");
            softly.assertThat(updatedEmployee.getDundieAwards()).isEqualTo(10);
            softly.assertThat(updatedEmployee.getOrganization()).isNull();
        });
    }
}
