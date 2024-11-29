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

class StreamProcessorTest {

    @Mock
    private ActivityService activityService;

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
                10,
                null
        );
        streamProcessor.process(event);
        verify(activityService).handleAwardOrganizationSuccessEvent(event);
    }

    @Test
    void shouldProcessSaveActivityAwardOrganizationSuccessEvent() {
        Event event = Event.createSaveActivityAwardOrganizationSuccessEvent(
                UUID.randomUUID(),
                Instant.now(),
                10,
                null, 
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
                10,
                1,
                null,
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
                10,
                null,
                null
        );
        streamProcessor.process(event);
        verify(awardService).handleSaveActivityAwardOrganizationFailureEvent(event);
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
