package com.ninjaone.dundie_awards.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.ActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
	public List<ActivityDto> getAllActivities() {
        log.info("getAllActivities - Fetching all activities from the database.");
        List<ActivityDto> activities = activityRepository.findAll()
                .stream()
                .map(ActivityDto::toDto)
                .toList();
        log.info("getAllActivities - Fetched {} activities.", activities.size());
        return activities;
    }
}
