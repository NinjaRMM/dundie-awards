package com.ninjaone.dundie_awards.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexViewController {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @GetMapping
    public String getIndex(Model model) {
        model.addAttribute("isDev", "dev".equalsIgnoreCase(activeProfile));
        return "index";
    }
    
}