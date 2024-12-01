package com.ninjaone.dundie_awards.dto;

import java.time.Instant;

import com.ninjaone.dundie_awards.model.Activity;

import lombok.Builder;

@Builder
public record ActivityDto(
        Instant occurredAt,
        String event) {

    public static ActivityDto toDto(Activity entity) {
        return ActivityDto.builder()
                .occurredAt(entity.getOccurredAt())
                .event(entity.getEvent())
                .build();
    }
}
