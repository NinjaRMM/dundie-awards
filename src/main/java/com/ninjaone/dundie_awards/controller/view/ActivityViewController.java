package com.ninjaone.dundie_awards.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.service.ActivityService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/activities")
@Controller
@RequiredArgsConstructor
public class ActivityViewController {
    
    private final ActivityService activityService;

    @GetMapping("/view")
    public String getActivities(Model model) {
        model.addAttribute("activities", activityService.getAllActivities());
        return "activities :: content";
    }

}
