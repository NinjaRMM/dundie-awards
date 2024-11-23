package com.ninjaone.dundie_awards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;


@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private MessageBroker messageBroker;

    @Autowired
    private AwardsCache awardsCache;
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @GetMapping
    public String getIndex(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("queueMessages", messageBroker.getMessages());
        model.addAttribute("totalDundieAwards", awardsCache.getTotalAwards());
        model.addAttribute("isDev", "dev".equalsIgnoreCase(activeProfile));
        return "index";
    }
}