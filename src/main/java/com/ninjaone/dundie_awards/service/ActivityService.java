package com.ninjaone.dundie_awards.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.repository.ActivityRepository;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<ActivityDto> getAllActivities() {
        return activityRepository.findAll()
                .stream().map(ActivityDto::toDto)
                .toList();
    }

}
