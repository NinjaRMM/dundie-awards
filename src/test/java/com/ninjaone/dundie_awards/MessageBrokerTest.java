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
                100
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
                50
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
}
