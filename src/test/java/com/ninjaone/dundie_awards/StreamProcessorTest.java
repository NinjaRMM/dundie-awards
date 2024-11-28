package com.ninjaone.dundie_awards;

import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.AwardService;
import com.ninjaone.dundie_awards.service.OrganizationService;

class StreamProcessorTest {

    @Mock
    private ActivityService activityService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private AwardService awardService;

    @InjectMocks
    private StreamProcessor streamProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessAwardOrganizationSuccessEvent() {
        Event event = Event.createAwardOrganizationSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                10,
                10
        );
        streamProcessor.process(event);
        verify(activityService).handleAwardOrganizationSuccessEvent(event);
    }

    @Test
    void shouldProcessSaveActivityAwardOrganizationSuccessEvent() {
        Event event = Event.createSaveActivityAwardOrganizationSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                null
        );
        streamProcessor.process(event);
        verify(awardService).handleSaveActivityAwardOrganizationSuccessEvent(event);
    }

    @Test
    void shouldProcessSaveActivityAwardOrganizationRetryEvent() {
        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                null
        );
        streamProcessor.process(event);
        verify(activityService).handleSaveActivityAwardOrganizationRetryEvent(event);
    }

    @Test
    void shouldProcessSaveActivityAwardOrganizationFailureEvent() {
        Event event = Event.createSaveActivityAwardOrganizationFailureEvent(
                UUID.randomUUID(),
                Instant.now(),
                null
        );
        streamProcessor.process(event);
        verify(awardService).handleSaveActivityAwardOrganizationFailureEvent(event);
    }

    @Test
    void shouldProcessUnblockOrganizationSuccessEvent() {
        Event event = Event.createUnblockOrganizationSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                null
        );
        streamProcessor.process(event);
        verify(organizationService).handleUnblockOrganizationSuccessEvent(event);
    }

    @Test
    void shouldProcessUnblockOrganizationRetryEvent() {
        Event event = Event.createUnblockOrganizationRetryEvent(
                UUID.randomUUID(),
                Instant.now(),
                1,
                null
        );
        streamProcessor.process(event);
        verify(organizationService).handleUnblockOrganizationRetryEvent(event);
    }

    @Test
    void shouldProcessUnblockOrganizationFailureEvent() {
        Event event = Event.createUnblockOrganizationFailureEvent(
                UUID.randomUUID(),
                Instant.now(),
                null
        );
        streamProcessor.process(event);
        verify(organizationService).handleUnblockOrganizationFailureEvent(event);
    }

    @Test
    void shouldProcessAwardOrganizationRollbackSuccessEvent() {
        Event event = Event.createAwardOrganizationRollbackSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                10,
                10
        );
        streamProcessor.process(event);
        verify(awardService).handleAwardOrganizationRollbackSuccessEvent(event);
    }

    @Test
    void shouldProcessAwardOrganizationRollbackRetryEvent() {
        Event event = Event.createAwardOrganizationRollbackRetryEvent(
                UUID.randomUUID(),
                Instant.now(),
                1
        );
        streamProcessor.process(event);
        verify(awardService).handleAwardOrganizationRollbackRetryEvent(event);
    }

    @Test
    void shouldProcessAwardOrganizationRollbackFailureEvent() {
        Event event = Event.createAwardOrganizationRollbackFailureEvent(
                UUID.randomUUID(),
                Instant.now()
        );
        streamProcessor.process(event);
        verify(awardService).handleAwardOrganizationRollbackFailureEvent(event);
    }
}
