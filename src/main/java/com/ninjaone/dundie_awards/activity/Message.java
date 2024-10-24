package com.ninjaone.dundie_awards.activity;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record Message(
        @NotNull
        Instant occurredAt,
        @NotNull
        String event
) {
    public static Message createMessageNow(String event) {
        return new Message(
                Instant.now(),
                event
        );
    }

    public Activity toActivity() {
        return new Activity(
                occurredAt,
                event
        );
    }
}
