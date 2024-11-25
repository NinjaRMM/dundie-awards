package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.impl.ActivityServiceImpl;

class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityServiceImpl activityService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllActivitiesAsDto() {
        Activity activity1 = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization 1")
                .build();
        Activity activity2 = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization 2")
                .build();
        List<Activity> activities = List.of(activity1, activity2);
        given(activityRepository.findAll()).willReturn(activities);

        List<ActivityDto> result = activityService.getAllActivities();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).occurredAt()).isEqualTo(activity1.getOccurredAt());
        assertThat(result.get(0).event()).isEqualTo(activity1.getEvent());
        assertThat(result.get(1).occurredAt()).isEqualTo(activity2.getOccurredAt());
        assertThat(result.get(1).event()).isEqualTo(activity2.getEvent());
    }


}
