package com.ninjaone.dundie_awards.dto;

import static java.util.Optional.ofNullable;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.service.OrganizationService;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder(toBuilder = true)
public record EmployeeUpdateRequestDto(
        @Nullable
        String firstName,
        @Nullable
        String lastName,
        @Nullable
        int dundieAwards,
        @Nullable
        @Min(1)
        Long organizationId
) {
	
	public Employee toEntity(Employee entity, OrganizationService organizationService) {
        return entity.toBuilder()
                .organization(
                    ofNullable(organizationId)
                        .map(organizationService::getValidOrganization)
                        .orElse(null)
                )
                .firstName(ofNullable(firstName).orElse(null))
                .lastName(ofNullable(lastName).orElse(null))
                .dundieAwards(dundieAwards)
                .build();
    }
}
