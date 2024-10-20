package com.ninjaone.dundie_awards.employee;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmployeeRecord(
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
                .firstName(db.getFirstName())
                .lastName(db.getLastName())
                .organizationId(db.getOrganization().getId())
                .organizationName(db.getOrganization().getName())
                .build();
    }
}
