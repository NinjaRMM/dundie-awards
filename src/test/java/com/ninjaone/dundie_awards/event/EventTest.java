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
        Organization organization = new Organization();

        Event event = Event.createAwardOrganizationSuccessEvent(
                uuid, occurredAt, totalAffectedEmployees, totalAwards, organization
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.totalAffectedEmployees()).isEqualTo(totalAffectedEmployees);
        assertThat(event.totalAwards()).isEqualTo(totalAwards);
        assertThat(event.organization()).isEqualTo(organization);
        assertThat(event.attempt()).isNull();
        assertThat(event.activity()).isNull();
    }

    @Test
    void shouldCreateSaveActivityAwardOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int totalAwards = 50;
        Activity activity = new Activity();

        Event event = Event.createSaveActivityAwardOrganizationSuccessEvent(
                uuid, occurredAt, totalAwards, activity, null
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.totalAwards()).isEqualTo(totalAwards);
        assertThat(event.activity()).isEqualTo(activity);
        assertThat(event.totalAffectedEmployees()).isNull();
        assertThat(event.attempt()).isNull();
        assertThat(event.organization()).isNull();
    }

    @Test
    void shouldCreateSaveActivityAwardOrganizationRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int totalAwards = 50;
        int attempt = 3;
        Activity activity = new Activity();
        Organization organization = new Organization();

        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid, occurredAt, totalAwards, attempt, activity, organization
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_RETRY_EVENT);
        assertThat(event.totalAwards()).isEqualTo(totalAwards);
        assertThat(event.attempt()).isEqualTo(attempt);
        assertThat(event.activity()).isEqualTo(activity);
        assertThat(event.organization()).isEqualTo(organization);
    }

    @Test
    void shouldCreateSaveActivityAwardOrganizationFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        int totalAwards = 50;
        Activity activity = new Activity();
        Organization organization = new Organization();

        Event event = Event.createSaveActivityAwardOrganizationFailureEvent(
                uuid, occurredAt, totalAwards, activity, organization
        );

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.eventType()).isEqualTo(EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_FAILURE_EVENT);
        assertThat(event.totalAwards()).isEqualTo(totalAwards);
        assertThat(event.activity()).isEqualTo(activity);
        assertThat(event.organization()).isEqualTo(organization);
    }

    @Test
    void shouldConvertEventToJsonAndBack() {
        UUID uuid = UUID.randomUUID();
        Event originalEvent = Event.createAwardOrganizationSuccessEvent(
                uuid, Instant.now(), 50, 50, new Organization()
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

        Event event = Event.fromJson(json);

        assertThat(event.uuid()).isEqualTo(uuid);
        assertThat(event.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(event.totalAffectedEmployees()).isEqualTo(100);
        assertThat(event.totalAwards()).isEqualTo(50);
    }
    
    @Test
    void shouldCreateFailureActivity() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().name("Test Organization").build();
        Event event = Event.createSaveActivityAwardOrganizationFailureEvent(
                uuid, Instant.now(), 10, new Activity(), organization
        );

        Activity activity = event.toFailureActivity();

        assertThat(activity.getEvent()).isEqualTo("Failure when trying to awards organization: Test Organization.");
        assertThat(activity.getOccurredAt()).isNotNull();
    }

    @Test
    void shouldCreateFailureRollbackActivity() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().name("Test Organization").build();
        Event event = Event.createAwardOrganizationRollbackFailureEvent(
                uuid, Instant.now(), 10, new Activity(), organization
        );

        Activity activity = event.toFailureRollback();

        assertThat(activity.getEvent()).isEqualTo("Failure when trying to rollback organization: Test Organization.");
        assertThat(activity.getOccurredAt()).isNotNull();
    }


    @Test
    void shouldCreateRetryActivity() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().name("Test Organization").build();
        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid, Instant.now(), 10, 1, new Activity(), organization
        );

        Activity activity = event.toRetryActivity();

        assertThat(activity.getEvent()).isEqualTo("Failure occurred. Retry confirm awards organization: Test Organization. Attempt: 1");
        assertThat(activity.getOccurredAt()).isNotNull();
    }

    @Test
    void shouldCreateRetryRollbackActivity() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().name("Test Organization").build();
        Event event = Event.createAwardOrganizationRollbackRetryEvent(
                uuid, Instant.now(), 10, 1, new Activity(), organization
        );

        Activity activity = event.toRetryRollback();

        assertThat(activity.getEvent()).isEqualTo("Retry rollback for organization: Test Organization. Attempt: 1");
        assertThat(activity.getOccurredAt()).isNotNull();
    }

    @Test
    void shouldCreateAddCacheActivity() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().name("Test Organization").build();
        Event event = Event.createAwardOrganizationSuccessEvent(
                uuid, Instant.now(), 100, 10, organization
        );

        Activity activity = event.toAddCacheActivity();

        assertThat(activity.getEvent()).isEqualTo("Cache increased after awards organization: Test Organization. Total employees awarded: 10");
        assertThat(activity.getOccurredAt()).isNotNull();
    }

    @Test
    void shouldCreateRemoveCacheActivity() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().name("Test Organization").build();
        Event event = Event.createAwardOrganizationRollbackSuccessEvent(
                uuid, Instant.now(), 10, new Activity(), organization
        );

        Activity activity = event.toRemoveCacheActivity();

        assertThat(activity.getEvent()).isEqualTo("Cache decreased after rollback for organization: Test Organization. Total employees affected: 10");
        assertThat(activity.getOccurredAt()).isNotNull();
    }

}
