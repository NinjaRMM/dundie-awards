package com.ninjaone.dundie_awards.dto;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import static java.util.Optional.ofNullable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmployeeDto(
        @NotNull
        Long id,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        @Min(1)
        int dundieAwards,
        @NotNull
        Long organizationId,
        @NotNull
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
