package com.ninjaone.dundie_awards.activity;

import com.ninjaone.dundie_awards.infrastructure.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activities")
public class Activity extends AbstractEntity {

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @Column(name = "event")
    private String event;
}
