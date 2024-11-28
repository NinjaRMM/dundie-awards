package com.ninjaone.dundie_awards.dto;

import static java.util.Optional.ofNullable;

import com.ninjaone.dundie_awards.model.Organization;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrganizationDto(
		@Nullable
		@Min(0)
        Long id,
        @NotNull
        @NotBlank
        String name,
        @NotNull
        boolean blocked,
        @Nullable
        String blockedBy
) {

    public static OrganizationDto toDto(Organization entity) {
        return OrganizationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .blocked(entity.isBlocked())
                .blockedBy(
                		ofNullable(entity.getBlockedBy()).
                		orElse(null))
                .build();
    }

   public  Organization toEntity() {
        return Organization.builder()
                .id(id)
                .name(name)
                .blocked(blocked)
                .blockedBy(
                		ofNullable(blockedBy)
                        .orElse(null)
                )
                .build();
    }
}
