package com.ninjaone.dundie_awards.activity;

import com.ninjaone.dundie_awards.config.DundieProperties;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class MessageBroker {

    private final DundieProperties dundieProperties;

    private final LinkedBlockingQueue<Message> messages;

    public MessageBroker(DundieProperties dundieProperties) {
        this.dundieProperties = dundieProperties;

        messages = new LinkedBlockingQueue<>(dundieProperties.messageBrokerSize());
    }

    // TODO 2024-10-21 Dom - Handle InterruptedException better
    // TODO 2024-10-21 Dom - Consider a retry/backoff on failure
    @SneakyThrows
    public void sendMessage(Message message) {
        boolean success = messages.offer(message, dundieProperties.messageBrokerTimeoutMs(), MILLISECONDS);
        if (!success) {
            throw new MessageBrokerTimeoutException();
        }
    }

    public List<Message> getMessages() {
        return messages.stream().toList();
    }
}
