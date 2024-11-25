package com.ninjaone.dundie_awards.dto;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;

class EmployeeDtoTest {

    @Test
    void shouldCheckAllValuesAreCopied() {
        Employee source = Employee.builder()
                .id(1L)
                .firstName("Ryan")
                .lastName("Howard")
                .dundieAwards(10)
                .organization(
                        Organization.builder()
                                .id(2L)
                                .name("Squanchy")
                                .build()
                )
                .build();
        
        EmployeeDto result = EmployeeDto.toDto(source);
        
        //assert EmployeeDto equals Entity
        assertSoftly(softly -> {
            softly.assertThat(result.id()).isEqualTo(source.getId());
            softly.assertThat(result.firstName()).isEqualTo(source.getFirstName());
            softly.assertThat(result.lastName()).isEqualTo(source.getLastName());
            softly.assertThat(result.dundieAwards()).isEqualTo(source.getDundieAwards());

            if (source.getOrganization() != null) {
                softly.assertThat(result.organizationId()).isEqualTo(source.getOrganization().getId());
                softly.assertThat(result.organizationName()).isEqualTo(source.getOrganization().getName());
            } else {
                softly.assertThat(result.organizationId()).isNull();
                softly.assertThat(result.organizationName()).isNull();
            }
            softly.assertThat(result.id()).isNotNull();
            softly.assertThat(result.firstName()).isNotNull();
            softly.assertThat(result.lastName()).isNotNull();
            softly.assertThat(result.dundieAwards()).isNotNull();
        });
    }

    @Test
    void shouldHandleNullOrganization() {
        Employee source = Employee.builder()
                .id(1L)
                .firstName("Ryan")
                .lastName("Howard")
                .dundieAwards(10)
                .organization(null)
                .build();

        EmployeeDto result = EmployeeDto.toDto(source);
        assertSoftly(softly -> {
            softly.assertThat(result.id()).isEqualTo(source.getId());
            softly.assertThat(result.firstName()).isEqualTo(source.getFirstName());
            softly.assertThat(result.lastName()).isEqualTo(source.getLastName());
            softly.assertThat(result.dundieAwards()).isEqualTo(source.getDundieAwards());
            softly.assertThat(result.organizationId()).isNull();
            softly.assertThat(result.organizationName()).isNull();
        });
    }

}
