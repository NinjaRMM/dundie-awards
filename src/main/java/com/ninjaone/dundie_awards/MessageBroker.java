package com.ninjaone.dundie_awards;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ninjaone.dundie_awards.event.Event;

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
    
}

