package com.ninjaone.dundie_awards.event;

import static java.lang.String.format;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;

public record Event(
        @NotNull UUID uuid,
        @NotNull Instant occurredAt,
        @NotNull EventType eventType,
        @Nullable Integer totalAffectedEmployees,
        @Nullable Integer totalAwards,
        @Nullable Integer attempt,
        @Nullable Activity activity,
        @Nullable Organization organization
) {
	private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
	
	@SneakyThrows
    public String toJson() {
        return objectMapper.writeValueAsString(this);
    }
	
	public Activity toActivity() {
		return Activity.builder()
				.event(format("Successfully awards organization: %s. Total employees awarded: %d",
						organization.getName(),
						totalAffectedEmployees))
				.occurredAt(Instant.now())
				.build();
	}
	
	public Activity toRetryActivity() {
		return Activity.builder()
				.event(format("Retry awards organization: %s. Attempt: %d",
						organization.getName(),
						calculatedAttempt()))
				.occurredAt(Instant.now())
				.build();
	}
	
	public Activity toUpdateCacheActivity() {
		return Activity.builder()
				.event(format("Cache updated after awards organization: %s. Total employees awarded: %d",
						organization.getName(),
						totalAwards))
				.occurredAt(Instant.now())
				.build();
	}

	public Activity toFailureActivity() {
		return Activity.builder()
				.event(format("Failure when trying to awards organization: %s.",
						organization.getName(),
						calculatedAttempt()))
				.occurredAt(Instant.now())
				.build();
	}
	
	@JsonIgnore
	public int calculatedAttempt() {
	    return attempt == null ? 0 : attempt;
	}
	
	@SneakyThrows
    public static Event fromJson(String json) {
        return objectMapper.readValue(json, Event.class);
    }
	
    // Factory for AWARD_ORGANIZATION_SUCCESS_EVENT
    public static Event createAwardOrganizationSuccessEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt,
            @NotNull Integer totalAffectedEmployees,
            @NotNull Integer totalAwards,
            @NotNull Organization organization
    ) {
        return new Event(uuid, occurredAt, EventType.AWARD_ORGANIZATION_SUCCESS_EVENT,
                totalAffectedEmployees, totalAwards, null, null, organization);
    }

    // Factory for SAVE_ACTIVITY_AWARD_ORGANIZATION_SUCCESS_EVENT
    public static Event createSaveActivityAwardOrganizationSuccessEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt,
            @NotNull Integer totalAwards,
            @NotNull Activity activity,
            @NotNull Organization organization
    ) {
        return new Event(uuid, occurredAt, EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_SUCCESS_EVENT,
                null, totalAwards, null, activity, organization);
    }

    // Factory for SAVE_ACTIVITY_AWARD_ORGANIZATION_RETRY_EVENT
    public static Event createSaveActivityAwardOrganizationRetryEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt,
            @NotNull Integer totalAwards,
            @NotNull Integer attempt,
            @NotNull Activity activity,
            @NotNull Organization organization
    ) {
        return new Event(uuid, occurredAt, EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_RETRY_EVENT,
                null, totalAwards, attempt, activity, organization);
    }

    // Factory for SAVE_ACTIVITY_AWARD_ORGANIZATION_FAILURE_EVENT
    public static Event createSaveActivityAwardOrganizationFailureEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt,
            @NotNull Integer totalAwards,
            @NotNull Activity activity,
            @NotNull Organization organization
    ) {
        return new Event(uuid, occurredAt, EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_FAILURE_EVENT,
                null, totalAwards, null, activity, organization);
    }

    // Factory for AWARD_ORGANIZATION_ROLLBACK_SUCCESS_EVENT
    public static Event createAwardOrganizationRollbackSuccessEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt,
            @NotNull Integer totalAffectedEmployees,
            @NotNull Integer totalAwards
    ) {
        return new Event(uuid, occurredAt, EventType.AWARD_ORGANIZATION_ROLLBACK_SUCCESS_EVENT,
                totalAffectedEmployees, totalAwards, null, null, null);
    }

    // Factory for AWARD_ORGANIZATION_ROLLBACK_RETRY_EVENT
    public static Event createAwardOrganizationRollbackRetryEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt,
            @NotNull Integer attempt
    ) {
        return new Event(uuid, occurredAt, EventType.AWARD_ORGANIZATION_ROLLBACK_RETRY_EVENT,
                null, null, attempt, null, null);
    }

    // Factory for AWARD_ORGANIZATION_ROLLBACK_FAILURE_EVENT
    public static Event createAwardOrganizationRollbackFailureEvent(
            @NotNull UUID uuid,
            @NotNull Instant occurredAt
    ) {
        return new Event(uuid, occurredAt, EventType.AWARD_ORGANIZATION_ROLLBACK_FAILURE_EVENT,
                null, null, null, null, null);
    }
    
    
}
