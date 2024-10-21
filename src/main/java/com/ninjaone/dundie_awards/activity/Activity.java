package com.ninjaone.dundie_awards.activity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(NONE)
    private long id;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @Column(name = "event")
    private String event;

    public Activity(Instant occurredAt, String event) {
        this.occurredAt = occurredAt;
        this.event = event;
    }
}
