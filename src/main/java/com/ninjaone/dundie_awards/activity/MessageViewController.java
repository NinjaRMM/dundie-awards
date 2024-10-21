package com.ninjaone.dundie_awards.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/messages")
public class MessageViewController {

    @GetMapping
    public String getMessages() {
        log.info("getMessages");
        return "messages :: content";
    }
}
