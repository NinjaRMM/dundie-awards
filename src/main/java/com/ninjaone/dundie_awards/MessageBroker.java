package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Activity;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class MessageBroker {

    private List<Activity> messages = new LinkedList<>();

    public void sendMessage(Activity message) {
        messages.add(message);
    }

    public List<Activity> getMessages(){
        return messages;
    }
}
