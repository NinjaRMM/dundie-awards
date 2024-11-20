package com.ninjaone.dundie_awards.events;

import com.ninjaone.dundie_awards.dto.ActivityEventDTO;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ActivityEventListener {

    @Autowired
    private ActivityService activityService;

    @Async
    @EventListener
    public void handleEvent(ActivityEventDTO activity) {
        System.out.println("Processing activity: " + activity.getEvent());
        activityService.logActivity(activity);
    }
}
