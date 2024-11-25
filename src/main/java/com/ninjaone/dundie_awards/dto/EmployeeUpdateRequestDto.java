package com.ninjaone.dundie_awards.dto;

import static java.util.Optional.ofNullable;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.service.OrganizationService;
import com.ninjaone.dundie_awards.validation.ValidOrganizationId;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmployeeUpdateRequestDto(
		@Nullable
        @NotBlank
        String firstName,
        @Nullable
        @NotBlank
        String lastName,
        @Nullable
        @Min(0)
        Integer dundieAwards,
        @Nullable
        @ValidOrganizationId
        Long organizationId
) {
	
	public Employee toEntity(Employee entity, OrganizationService organizationService) {
        return entity.toBuilder()
                .organization(
                    ofNullable(organizationId)
                        .map(organizationService::getOrganization)
                        .orElse(null)
                )
                .firstName(ofNullable(firstName).orElse(null))
                .lastName(ofNullable(lastName).orElse(null))
                .dundieAwards(dundieAwards)
                .build();
    }
}
