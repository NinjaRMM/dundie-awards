package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

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
        
        when(activityRepository.findAll(any(Sort.class))).thenReturn(activities);

        List<ActivityDto> result = activityService.getAllActivities();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).occurredAt()).isEqualTo(activity1.getOccurredAt());
        assertThat(result.get(0).event()).isEqualTo(activity1.getEvent());
        assertThat(result.get(1).occurredAt()).isEqualTo(activity2.getOccurredAt());
        assertThat(result.get(1).event()).isEqualTo(activity2.getEvent());
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

        Activity activity1 = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization")
                .build();

        Activity activity2 = Activity.builder()
                .occurredAt(Instant.now())
                .event("Successfully awards organization: Test Organization. Total employees awarded: 50")
                .build();

        // Mocking save behavior
        when(activityRepository.save(any(Activity.class))).thenReturn(activity1, activity2);

        // Execute the method
        activityService.handleAwardOrganizationSuccessEvent(event);

        // Capture and verify multiple calls to save
        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository, times(2)).save(activityCaptor.capture());

        List<Activity> savedActivities = activityCaptor.getAllValues();

        // Verify the first saved activity
        assertThat(savedActivities.get(0).getEvent()).isEqualTo("Successfully awards organization: Test Organization. Total employees awarded: 50");

        // Verify the second saved activity
        assertThat(savedActivities.get(1).getEvent())
                .isEqualTo("ACTIVITY ACK SUCCESS Organization: Test Organization. Total employees awarded: 50");

        // Verify message publishing
        verify(messageBroker).publishSaveActivityAwardOrganizationSuccessEvent(
                eq(uuid),
                eq(activity2),
                eq(50),
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

        when(appProperties.retrySaveActivity()).thenReturn(new AppProperties.RetrySaveActivity(3, 500));
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
    
    @Test
    void shouldHandleSaveActivityAwardOrganizationRetryEventAndFail() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Activity activity = Activity.builder().occurredAt(Instant.now()).event("Retry Activity").build();

        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid, Instant.now(), 50, 3, activity, organization
        );

        when(appProperties.retrySaveActivity()).thenReturn(new AppProperties.RetrySaveActivity(3, 500));

        activityService.handleSaveActivityAwardOrganizationRetryEvent(event);

        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    void shouldHandleFailureAtActivityCreationWithRetry() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Activity activity = Activity.builder().occurredAt(Instant.now()).event("Activity Failure").build();

        Event event = Event.createSaveActivityAwardOrganizationRetryEvent(
                uuid, Instant.now(), 50, 1, activity, organization
        );

        when(appProperties.retrySaveActivity()).thenReturn(new AppProperties.RetrySaveActivity(3, 500));
        when(activityRepository.save(any(Activity.class))).thenThrow(new RuntimeException("Simulated Failure"));

        Method method = ActivityServiceImpl.class.getDeclaredMethod(
                "failureAtActivityCreation", Event.class, Activity.class, Organization.class, int.class
        );
        method.setAccessible(true);

        try {
            method.invoke(activityService, event, activity, organization, 50);
        } catch (InvocationTargetException e) {
            assertThat(e.getCause()).isInstanceOf(RuntimeException.class).hasMessage("Simulated Failure");
        }

    }



    @Test
    void shouldHandleExtractActivityFromEventSuccessfully() throws Exception {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();

        Event successEvent = Event.createAwardOrganizationSuccessEvent(
                uuid, Instant.now(), 50, 50, organization
        );

        Method method = ActivityServiceImpl.class.getDeclaredMethod("extractActivityFromEvent", Event.class);
        method.setAccessible(true);
        Activity result = (Activity) method.invoke(activityService, successEvent);

        assertThat(result.getEvent()).contains("ACTIVITY ACK SUCCESS Organization: Test Organization. Total employees awarded: 50");
    }

}
