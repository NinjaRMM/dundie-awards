package com.ninjaone.dundie_awards.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

import static com.ninjaone.dundie_awards.activity.ActivityRecord.createActivityNow;

@Slf4j
@RequestMapping("/activities")
@Controller
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/view")
    public String getActivities(Model model) {
        log.trace("getActivities");
        model.addAttribute("activities", activityService.findAll());
        return "activities :: content";
    }

    @PostMapping
    @ResponseBody
    public void newActivity() {
        log.trace("newActivity");
        activityService.createActivity(
                createActivityNow(
                        "New random activity: " + UUID.randomUUID()
                )
        );
    }
}
