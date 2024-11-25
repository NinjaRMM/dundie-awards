package com.ninjaone.dundie_awards.dto;

import static java.util.Optional.ofNullable;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.validation.ValidOrganizationId;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmployeeDto(
		@Nullable
		@Min(0)
        Long id,
        @NotNull
        @NotBlank
        String firstName,
        @NotNull
        @NotBlank
        String lastName,
        @NotNull
        @Min(0)
        int dundieAwards,
        @Nullable
        @ValidOrganizationId
        Long organizationId,
        @Nullable
        String organizationName
) {

    public static EmployeeDto toDto(Employee entity) {
        return EmployeeDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .dundieAwards(entity.getDundieAwards())
                .organizationId(ofNullable(entity.getOrganization()).map(Organization::getId).orElse(null))
                .organizationName(ofNullable(entity.getOrganization()).map(Organization::getName).orElse(null))
                .build();
    }

   public  Employee toEntity() {
        return Employee.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .dundieAwards(dundieAwards)
                .organization(
                		ofNullable(organizationId)
                        .map(id -> Organization.builder().id(id).build())
                        .orElse(null)
                )
                .build();
    }
}
