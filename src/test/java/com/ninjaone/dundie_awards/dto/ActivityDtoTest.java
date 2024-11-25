package com.ninjaone.dundie_awards.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.ninjaone.dundie_awards.model.Activity;

class ActivityDtoTest {

    @Test
    void shouldConvertActivityToActivityDto() {
        Instant occurredAt = Instant.now();
        String event = "Award given to Organization";
        Activity activity = Activity.builder()
                .occurredAt(occurredAt)
                .event(event)
                .build();


        ActivityDto activityDto = ActivityDto.toDto(activity);

        assertThat(activityDto).isNotNull();
        assertThat(activityDto.occurredAt()).isEqualTo(occurredAt);
        assertThat(activityDto.event()).isEqualTo(event);
    }

}
