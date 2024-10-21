package com.ninjaone.dundie_awards.activity;

import java.time.Instant;

public record Message(
        Instant occurredAt,
        String event
) {
    public static Message createMessageNow(String event) {
        return new Message(
                Instant.now(),
                event
        );
    }
}
