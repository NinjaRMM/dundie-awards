package com.ninjaone.dundie_awards.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/messages")
@Controller
public class MessageController {

    @Autowired
    private MessageBroker messageBroker;

    @GetMapping("/view")
    public String getMessages(Model model) {
        log.trace("getMessages");
        model.addAttribute("queueMessages", messageBroker.getMessages());
        return "messages :: content";
    }
}
