package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.impl.ActivityServiceImpl;

class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private MessageBroker messageBroker;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AwardOperationLogService awardOperationLogService;

    @Mock
    private OrganizationService organizationService;

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
        when(activityRepository.findAll()).thenReturn(activities);

        List<ActivityDto> result = activityService.getAllActivities();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).occurredAt()).isEqualTo(activity1.getOccurredAt());
        assertThat(result.get(0).event()).isEqualTo(activity1.getEvent());
        assertThat(result.get(1).occurredAt()).isEqualTo(activity2.getOccurredAt());
        assertThat(result.get(1).event()).isEqualTo(activity2.getEvent());
    }

    @Test
    void shouldCreateActivitySuccessfully() {
        Activity activity = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization")
                .build();

        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity createdActivity = activityService.createActivity(activity);

        assertThat(createdActivity).isNotNull();
        assertThat(createdActivity.getOccurredAt()).isEqualTo(activity.getOccurredAt());
        assertThat(createdActivity.getEvent()).isEqualTo(activity.getEvent());
        verify(activityRepository).save(activity);
    }

    @Test
    void shouldHandleAwardOrganizationSuccessEventSuccessfully() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Event event = Event.createAwardOrganizationSuccessEvent(
                uuid,
                Instant.now(),
                50,
                50,
                organization
        );

        Activity activity = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization")
                .build();

        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(appProperties.retrySaveActivity()).thenReturn(new AppProperties.RetrySaveActivityConfig(3, 1000));

        activityService.handleAwardOrganizationSuccessEvent(event);

        verify(activityRepository).save(any(Activity.class));
        verify(messageBroker).publishSaveActivityAwardOrganizationSuccessEvent(
                eq(uuid),
                eq(activity),
                eq(event.totalAwards()),
                eq(organization)
        );
    }

    @Test
    void shouldFinalizeActivityTransactionSuccessfully() throws Exception {
        UUID uuid = UUID.randomUUID();
        Activity activity = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization")
                .build();
        Organization organization = Organization.builder()
                .id(1L)
                .name("Test Organization")
                .build();
        Integer totalAwards = 50;

        when(activityRepository.save(activity)).thenReturn(activity);

        // Access private method using reflection
        Method method = ActivityServiceImpl.class.getDeclaredMethod(
                "finalizeActivityTransaction", UUID.class, Integer.class, Activity.class, Organization.class, Event.class);
        method.setAccessible(true);

        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid,
                Instant.now(),
                totalAwards,
                1,
                activity,
                organization
        );

        method.invoke(activityService, uuid, totalAwards, activity, organization, event);

        verify(activityRepository).save(activity);
        verify(awardOperationLogService).cleanAwardOperationLog(uuid);
        verify(organizationService).unblock(uuid, organization);
        verify(messageBroker).publishSaveActivityAwardOrganizationSuccessEvent(uuid, activity, totalAwards, organization);
    }
    
    @Test
    void shouldHandleSaveActivityAwardOrganizationRetryEventWithBackoff() throws InterruptedException {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid,
                Instant.now(),
                50,
                2,
                Activity.builder().build(),
                organization
        );

        when(appProperties.retrySaveActivity()).thenReturn(new AppProperties.RetrySaveActivityConfig(3, 500));
        when(activityRepository.save(any(Activity.class))).thenReturn(Activity.builder().build());

        activityService.handleSaveActivityAwardOrganizationRetryEvent(event);

        verify(activityRepository).save(any(Activity.class));
        verify(messageBroker).publishSaveActivityAwardOrganizationSuccessEvent(
                eq(uuid),
                any(Activity.class),
                eq(50),
                eq(organization)
        );
    }
}
