package com.ninjaone.dundie_awards.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "award_operation_log")
public class AwardOperationLog {
	
	@EqualsAndHashCode.Include
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
	@Setter(AccessLevel.NONE)
    private String uuid;
	
	@Column(name = "occurred_at")
    private Instant occurredAt;

    @JoinColumn(name = "rollback_data")
    @Lob
    private String rollbackData;

}
