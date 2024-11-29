package com.ninjaone.dundie_awards;

import static java.util.Optional.ofNullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageBroker {

    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    private final ApplicationEventPublisher eventPublisher;
    
    public List<Event> getUnprocessedEvents() {
        return queue.stream().toList();
    }

    public void sendMessage(Event message) {
    	log.info("UUID: {} - sendMessage - Adding message to queue: {}", message.uuid(), message.toJson());
        queue.add(message);
        log.info("UUID: {} - sendMessage - Added message to queue: {}", message.uuid(), message.toJson());
    }

    @Scheduled(fixedRateString = "${app.messageProcessingIntervalMs}") 
    @SneakyThrows
    public void processQueue() {
    	ofNullable(!queue.isEmpty() ? queue.poll() : null)
        	.ifPresent(ev->{
        		log.info("UUID: {} - Publishing event -> {}",ev.uuid(),ev.toJson());
        		eventPublisher.publishEvent(ev);
        	});
    }
    
    public void publishAwardOrganizationSuccessEvent(UUID uuid, Instant occurredAt, Integer totalUpdatedRecords, Integer totalAwards, Organization organization) {
        sendMessage(
            Event.createAwardOrganizationSuccessEvent(
                uuid,
                occurredAt,
                totalUpdatedRecords,
                totalAwards,
                organization
            )
        );
    }
    
    public void publishSaveActivityAwardOrganizationSuccessEvent(UUID uuid, Activity activity, Integer totalAwards, Organization organization) {
        sendMessage(
            Event.createSaveActivityAwardOrganizationSuccessEvent(
                uuid,
                Instant.now(),
                totalAwards,
                activity,
                organization
            )
        );
    }
    
    public void publishSaveActivityAwardOrganizationRetryEvent(UUID uuid, Integer totalAwards, Integer attempt, Activity activity, Organization organization) {
        sendMessage(
            Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid,
                Instant.now(),
                totalAwards,
                attempt,
                activity,
                organization
            )
        );
    }
    
    public void publishSaveActivityAwardOrganizationFailureEvent(UUID uuid, Integer totalAwards, Activity activity, Organization organization) {
        sendMessage(
            Event.createSaveActivityAwardOrganizationFailureEvent(
                uuid,
                Instant.now(),
                totalAwards,
                activity,
                organization                
            )
        );
    }
    
}

