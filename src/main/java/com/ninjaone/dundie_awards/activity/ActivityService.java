package com.ninjaone.dundie_awards.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    List<ActivityRecord> findAll() {
        return activityRepository.findAll()
                .stream().map(ActivityRecord::fromDb)
                .toList();
    }

    void createActivity(ActivityRecord activity) {
        activityRepository.save(activity.toDb());
    }

    public void handleMessage(Message message) {
        activityRepository.save(message.toActivity());
    }
}
