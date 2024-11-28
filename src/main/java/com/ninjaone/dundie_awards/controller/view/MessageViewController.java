package com.ninjaone.dundie_awards.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.MessageBroker;

import lombok.RequiredArgsConstructor;

@RequestMapping("/messages")
@Controller
@RequiredArgsConstructor
public class MessageViewController {

    private final MessageBroker messageBroker;

    @GetMapping("/view")
    public String getMessages(Model model) {
    	messageBroker.getUnprocessedEvents();
        model.addAttribute("queueMessages", messageBroker.getUnprocessedEvents());
        return "messages :: content";
    }

}
