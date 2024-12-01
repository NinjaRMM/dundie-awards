package com.ninjaone.dundie_awards.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.event.Event;

import lombok.RequiredArgsConstructor;

@RequestMapping("/messages")
@Controller
@RequiredArgsConstructor
public class MessageViewController {

    private final MessageBroker messageBroker;

    @GetMapping("/view")
    public String getMessages(Model model) {
		List<Event> unprocessedEvents = messageBroker.getUnprocessedEvents();
        model.addAttribute("queueMessages",unprocessedEvents.isEmpty()?null:unprocessedEvents);
        return "messages :: content";
    }

}
