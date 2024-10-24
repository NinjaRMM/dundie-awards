package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.organization.Organization;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmployeeRecord(
        @NotNull
        long id,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        int dundieAwards,
        @NotNull
        long organizationId,
        @NotNull
        String organizationName
) {

    static EmployeeRecord fromDb(Employee db) {
        return EmployeeRecord.builder()
                .id(db.getId())
                .firstName(db.getFirstName())
                .lastName(db.getLastName())
                .dundieAwards(db.getDundieAwards())
                .organizationId(db.getOrganization().getId())
                .organizationName(db.getOrganization().getName())
                .build();
    }

    Employee toDb() {
        return Employee.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .dundieAwards(dundieAwards)
                .organization(
                        Organization.builder()
                                .id(organizationId)
                                .build()
                )
                .build();
    }
}
