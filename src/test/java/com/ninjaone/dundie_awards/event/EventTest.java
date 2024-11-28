package com.ninjaone.dundie_awards.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;

class EventTest {

    @Test
    void shouldCreateAwardOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int totalAffectedEmployees = 100;
        int totalAwards = 50;

        Event event = Event.createAwardOrganizationSuccessEvent(
                uuid, occurredAt, totalAffectedEmployees, totalAwards
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.totalAffectedEmployees()).isEqualTo(totalAffectedEmployees);
        assertThat(event.totalAwards()).isEqualTo(totalAwards);
        assertThat(event.attempt()).isNull();
        assertThat(event.activity()).isNull();
        assertThat(event.organization()).isNull();
    }

    @Test
    void shouldCreateSaveActivityAwardOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        Activity activity = new Activity(); 

        Event event = Event.createSaveActivityAwardOrganizationSuccessEvent(
                uuid, occurredAt, activity
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.activity()).isEqualTo(activity);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.attempt()).isNull();
        assertThat(event.organization()).isNull();
    }

    @Test
    void shouldCreateSaveActivityAwardOrganizationRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int attempt = 3;
        Activity activity = new Activity(); 

        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid, occurredAt, attempt, activity
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_RETRY_EVENT);
        assertThat(event.attempt()).isEqualTo(attempt);
        assertThat(event.activity()).isEqualTo(activity);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.organization()).isNull();
    }

    @Test
    void shouldCreateSaveActivityAwardOrganizationFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        Activity activity = new Activity(); 

        Event event = Event.createSaveActivityAwardOrganizationFailureEvent(
                uuid, occurredAt, activity
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_FAILURE_EVENT);
        assertThat(event.activity()).isEqualTo(activity);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.attempt()).isNull();
        assertThat(event.organization()).isNull();
    }

    @Test
    void shouldCreateUnblockOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        Organization organization = new Organization(); 

        Event event = Event.createUnblockOrganizationSuccessEvent(
                uuid, occurredAt, organization
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.UNBLOCK_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.organization()).isEqualTo(organization);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.attempt()).isNull();
        assertThat(event.activity()).isNull();
    }

    @Test
    void shouldCreateUnblockOrganizationRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int attempt = 2;
        Organization organization = new Organization(); 

        Event event = Event.createUnblockOrganizationRetryEvent(
                uuid, occurredAt, attempt, organization
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.UNBLOCK_ORGANIZATION_RETRY_EVENT);
        assertThat(event.attempt()).isEqualTo(attempt);
        assertThat(event.organization()).isEqualTo(organization);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.activity()).isNull();
    }

    @Test
    void shouldCreateUnblockOrganizationFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        Organization organization = new Organization(); 

        Event event = Event.createUnblockOrganizationFailureEvent(
                uuid, occurredAt, organization
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.UNBLOCK_ORGANIZATION_FAILURE_EVENT);
        assertThat(event.organization()).isEqualTo(organization);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.attempt()).isNull();
        assertThat(event.activity()).isNull();
    }

    @Test
    void shouldCreateAwardOrganizationRollbackSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int totalAffectedEmployees = 75;
        int totalAwards = 25;

        Event event = Event.createAwardOrganizationRollbackSuccessEvent(
                uuid, occurredAt, totalAffectedEmployees, totalAwards
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_ROLLBACK_SUCCESS_EVENT);
        assertThat(event.totalAffectedEmployees()).isEqualTo(totalAffectedEmployees);
        assertThat(event.totalAwards()).isEqualTo(totalAwards);
        assertThat(event.activity()).isNull();
        assertThat(event.organization()).isNull();
        assertThat(event.attempt()).isNull();
    }

    @Test
    void shouldCreateAwardOrganizationRollbackRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int attempt = 1;

        Event event = Event.createAwardOrganizationRollbackRetryEvent(
                uuid, occurredAt, attempt
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_ROLLBACK_RETRY_EVENT);
        assertThat(event.attempt()).isEqualTo(attempt);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.activity()).isNull();
        assertThat(event.organization()).isNull();
    }

    @Test
    void shouldCreateAwardOrganizationRollbackFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        Event event = Event.createAwardOrganizationRollbackFailureEvent(
                uuid, occurredAt
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_ROLLBACK_FAILURE_EVENT);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.totalAwards()).isNull();
        assertThat(event.activity()).isNull();
        assertThat(event.organization()).isNull();
        assertThat(event.attempt()).isNull();
    }
    
    @Test
    void shouldConvertEventToJsonAndBack() {
    	UUID uuid = UUID.randomUUID();
        Event originalEvent = Event.createAwardOrganizationSuccessEvent(
        		uuid,
                Instant.now(),
                50,
                50
        );

        String json = originalEvent.toJson();
        Event deserializedEvent = Event.fromJson(json);

        assertThat(deserializedEvent).isEqualTo(originalEvent);
    }

    @Test
    void shouldDeserializeJsonToEvent() {
        UUID uuid = UUID.randomUUID();
        String json = """
        {
            "uuid": "%s",
            "occurredAt": "2024-11-28T12:00:00Z",
            "eventType": "AWARD_ORGANIZATION_SUCCESS_EVENT",
            "totalAffectedEmployees": 100,
            "totalAwards": 50,
            "attempt": null,
            "activity": null,
            "organization": null
        }
        """.formatted(uuid);

        // Act
        Event event = Event.fromJson(json);

        // Assert
        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.totalAffectedEmployees()).isEqualTo(100);
        assertThat(event.totalAwards()).isEqualTo(50);
    }
}
