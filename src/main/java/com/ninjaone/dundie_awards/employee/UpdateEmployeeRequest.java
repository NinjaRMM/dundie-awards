package com.ninjaone.dundie_awards.employee;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/*
 * Note: In reality, we'd probably only want to update first/lastName.
 * Applying changes to the other fields might be best served by changeOrganization() or
 * giftAward() type APIs, but left here for now.
 */
@Builder(toBuilder = true)
public record UpdateEmployeeRequest(
        @NotNull
        long id,
        @Nullable
        String firstName,
        @Nullable
        String lastName,
        @Nullable
        Integer dundieAwards,
        @Nullable
        @Min(1)
        Long organizationId
) {
}
