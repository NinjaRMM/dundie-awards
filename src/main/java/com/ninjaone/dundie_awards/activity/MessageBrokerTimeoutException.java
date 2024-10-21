package com.ninjaone.dundie_awards.activity;

import com.ninjaone.dundie_awards.error.DundieException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class MessageBrokerTimeoutException extends DundieException {
    public MessageBrokerTimeoutException() {
        super(
                INTERNAL_SERVER_ERROR,
                "dundie.timeout",
                "An internal timeout error occurred",
                "The message broker is overloaded and could not accept the message"
        );
    }
}
