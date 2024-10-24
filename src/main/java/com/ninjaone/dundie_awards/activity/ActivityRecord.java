package com.ninjaone.dundie_awards.activity;

import java.time.Instant;

public record ActivityRecord(
        Instant occurredAt,
        String event) {

    public Activity toDb() {
        return new Activity(
                occurredAt,
                event
        );
    }

    public static ActivityRecord fromDb(Activity activity) {
        return new ActivityRecord(
                activity.getOccurredAt(),
                activity.getEvent()
        );
    }

    public static ActivityRecord createActivityNow(String event) {
        return new ActivityRecord(
                Instant.now(),
                event
        );
    }
}
