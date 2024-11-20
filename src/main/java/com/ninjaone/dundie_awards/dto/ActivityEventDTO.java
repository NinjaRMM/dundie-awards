package com.ninjaone.dundie_awards.dto;

import java.time.LocalDateTime;

public class ActivityEventDTO {

    private LocalDateTime occuredAt;

    private String event;


    public ActivityEventDTO() {}

    public ActivityEventDTO(LocalDateTime occuredAt, String event) {
        this.occuredAt = occuredAt;
        this.event = event;
    }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public void setOccuredAt(LocalDateTime occuredAt) {
        this.occuredAt = occuredAt;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
