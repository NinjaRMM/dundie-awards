package com.ninjaone.dundie_awards.dto;

import java.time.Instant;

import com.ninjaone.dundie_awards.model.Activity;

public record ActivityDto(
        Instant occurredAt,
        String event) {

    public static ActivityDto toDto(Activity activity) {
        return new ActivityDto(
                activity.getOccurredAt(),
                activity.getEvent()
        );
    }

}
