package com.ninjaone.dundie_awards.events;

import com.ninjaone.dundie_awards.dto.ActivityEventDTO;
import com.ninjaone.dundie_awards.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MessageBroker {

    private Queue<ActivityEventDTO> messages = new ConcurrentLinkedQueue<>();

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void sendMessage(ActivityEventDTO message) {
        messages.add(message);
        eventPublisher.publishEvent(message);
    }

    public Queue<ActivityEventDTO> getMessages(){
        return messages;
    }
}
