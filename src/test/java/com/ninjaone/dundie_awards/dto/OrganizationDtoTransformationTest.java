package com.ninjaone.dundie_awards.dto;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ninjaone.dundie_awards.model.Organization;

class OrganizationDtoTransformationTest {

    @Test
    void shouldCheckAllValuesAreCopied() {
        Organization source = Organization.builder()
                .id(1L)
                .name("Tom")
                .blocked(true)
                .blockedBy(UUID.randomUUID().toString())
                .build();
        
        OrganizationDto result = OrganizationDto.toDto(source);
        
        assertSoftly(softly -> {
            softly.assertThat(result.id()).isEqualTo(source.getId());
            softly.assertThat(result.name()).isEqualTo(source.getName());
            softly.assertThat(result.blocked()).isEqualTo(source.isBlocked());
            softly.assertThat(result.blockedBy()).isEqualTo(source.getBlockedBy());
            softly.assertThat(result.id()).isNotNull();
            softly.assertThat(result.name()).isNotBlank();
        });
    }

    @Test
    void shouldHandleNullBlockedBy() {
        Organization source = Organization.builder()
                .id(2L)
                .name("Tom")
                .blocked(false)
                .blockedBy(null)
                .build();

        OrganizationDto result = OrganizationDto.toDto(source);
        assertSoftly(softly -> {
            softly.assertThat(result.id()).isEqualTo(source.getId());
            softly.assertThat(result.name()).isEqualTo(source.getName());
            softly.assertThat(result.blocked()).isEqualTo(source.isBlocked());
            softly.assertThat(result.blockedBy()).isNull();
        });
    }

    @Test
    void shouldConvertDtoToEntity() {
        OrganizationDto dto = OrganizationDto.builder()
                .id(3L)
                .name("Tom")
                .blocked(true)
                .blockedBy(UUID.randomUUID().toString())
                .build();

        Organization result = dto.toEntity();
        assertSoftly(softly -> {
            softly.assertThat(result.getId()).isEqualTo(dto.id());
            softly.assertThat(result.getName()).isEqualTo(dto.name());
            softly.assertThat(result.isBlocked()).isEqualTo(dto.blocked());
            softly.assertThat(result.getBlockedBy()).isEqualTo(dto.blockedBy());
        });
    }
}
