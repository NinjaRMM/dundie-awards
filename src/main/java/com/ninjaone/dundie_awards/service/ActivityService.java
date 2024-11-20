package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.ActivityEventDTO;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public void logActivity(ActivityEventDTO activityEvent) {
        activityRepository.save(
                new Activity(
                        activityEvent.getOccuredAt(),
                        activityEvent.getEvent()));
    }
}
