package com.ninjaone.dundie_awards.activity;

import com.ninjaone.dundie_awards.config.DundieProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Component
public class MessageBroker {

    private final DundieProperties dundieProperties;

    private final LinkedBlockingQueue<Message> messages;
    private final ActivityService activityService;

    public MessageBroker(ActivityService activityService, DundieProperties dundieProperties) {
        this.activityService = activityService;
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

    /**
     * Checks the message queue every five seconds (default) and handles messages, posting them to be handled
     * as Activities.
     */
    @SneakyThrows
    @Scheduled(fixedRate = 5000)
    public void handle() {
        if (!messages.isEmpty()) {
            log.trace("Handling messages...");
        }

        Optional.ofNullable(messages.poll(dundieProperties.messageBrokerTimeoutMs(), MILLISECONDS))
                .ifPresent(activityService::handleMessage);
    }

    public List<Message> getMessages() {
        return messages.stream().toList();
    }
}
