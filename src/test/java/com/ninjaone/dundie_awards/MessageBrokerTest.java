package com.ninjaone.dundie_awards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;

class MessageBrokerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MessageBroker messageBroker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageBroker = new MessageBroker(eventPublisher);
    }

    @Test
    void shouldSendAndRetrieveUnprocessedEvents() {
        Event event = Event.createAwardOrganizationSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                100,
                100,
                new Organization()
        );

        messageBroker.sendMessage(event);
        List<Event> unprocessedEvents = messageBroker.getUnprocessedEvents();

        assertThat(unprocessedEvents).hasSize(1);
        assertThat(unprocessedEvents.get(0)).isEqualTo(event);
    }

    @Test
    void shouldProcessQueueAndPublishEvent() throws InterruptedException {
        Event event = Event.createAwardOrganizationSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                50,
                50,
                new Organization()
        );
        messageBroker.sendMessage(event);
        messageBroker.processQueue();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent).isEqualTo(event);
    }

    @Test
    void shouldHandleEmptyQueueGracefully() {
        messageBroker.processQueue();

        then(eventPublisher).shouldHaveNoInteractions();
    }

    @Test
    void shouldPublishAwardOrganizationSuccessEvent() throws InterruptedException {
        UUID uuid = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        Organization organization = new Organization();

        messageBroker.publishAwardOrganizationSuccessEvent(uuid, occurredAt, 100, 200, organization);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAffectedEmployees()).isEqualTo(100);
        assertThat(publishedEvent.totalAwards()).isEqualTo(200);
        assertThat(publishedEvent.organization()).isEqualTo(organization);
    }


    @Test
    void shouldPublishSaveActivityAwardOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Activity activity = new Activity();
        Integer totalAwards = 200;

        messageBroker.publishSaveActivityAwardOrganizationSuccessEvent(uuid, activity, totalAwards, null);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAwards()).isEqualTo(totalAwards);
        assertThat(publishedEvent.activity()).isEqualTo(activity);
    }

    @Test
    void shouldPublishSaveActivityAwardOrganizationRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Integer attempt = 2;
        Integer totalAwards = 150;
        Activity activity = new Activity();
        Organization organization = new Organization();

        messageBroker.publishSaveActivityAwardOrganizationRetryEvent(uuid, totalAwards, attempt, activity, organization);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAwards()).isEqualTo(totalAwards);
        assertThat(publishedEvent.attempt()).isEqualTo(attempt);
        assertThat(publishedEvent.activity()).isEqualTo(activity);
        assertThat(publishedEvent.organization()).isEqualTo(organization);
    }

    @Test
    void shouldPublishSaveActivityAwardOrganizationFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Integer totalAwards = 150;
        Activity activity = new Activity();
        Organization organization = new Organization();

        messageBroker.publishSaveActivityAwardOrganizationFailureEvent(uuid, totalAwards, activity, organization);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAwards()).isEqualTo(totalAwards);
        assertThat(publishedEvent.activity()).isEqualTo(activity);
        assertThat(publishedEvent.organization()).isEqualTo(organization);
    }
    
    @Test
    void shouldPublishAwardOrganizationRollbackSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Integer totalAwards = 150;
        Activity activity = new Activity();
        Organization organization = new Organization();

        messageBroker.publishAwardOrganizationRollbackSuccessEvent(uuid, activity, totalAwards, organization);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAwards()).isEqualTo(totalAwards);
        assertThat(publishedEvent.activity()).isEqualTo(activity);
        assertThat(publishedEvent.organization()).isEqualTo(organization);
    }

    @Test
    void shouldPublishAwardOrganizationRollbackRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Integer totalAwards = 150;
        Integer attempt = 2;
        Activity activity = new Activity();
        Organization organization = new Organization();

        messageBroker.publishAwardOrganizationRollbackRetryEvent(uuid, totalAwards, attempt, activity, organization);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAwards()).isEqualTo(totalAwards);
        assertThat(publishedEvent.attempt()).isEqualTo(attempt);
        assertThat(publishedEvent.activity()).isEqualTo(activity);
        assertThat(publishedEvent.organization()).isEqualTo(organization);
    }

    @Test
    void shouldPublishAwardOrganizationRollbackFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Integer totalAwards = 150;
        Activity activity = new Activity();
        Organization organization = new Organization();

        messageBroker.publishAwardOrganizationRollbackFailureEvent(uuid, totalAwards, activity, organization);
        messageBroker.processQueue();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        Event publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.uuid()).isEqualTo(uuid);
        assertThat(publishedEvent.totalAwards()).isEqualTo(totalAwards);
        assertThat(publishedEvent.activity()).isEqualTo(activity);
        assertThat(publishedEvent.organization()).isEqualTo(organization);
    }

}
