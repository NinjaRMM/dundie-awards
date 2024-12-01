package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.AwardOperationLog;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.service.impl.AwardServiceImpl;

class AwardsServiceTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AwardOperationLogService awardOperationLogService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ActivityService activityService;

    @Mock
    private AwardsCache awardsCache;

    @Mock
    private MessageBroker messageBroker;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private AwardServiceImpl awardsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessAwardsSuccessfully() {
        UUID uuid = UUID.randomUUID();
        long organizationId = 1L;
        String rollbackData = "1,5|2,3|3,2";
        int updatedRecords = 10;
        Organization organization = Organization.builder().id(organizationId).name("Test Org").build();

        given(employeeService.fetchEmployeeRollbackData(uuid, organizationId)).willReturn(rollbackData);
        given(employeeService.addDundieAwardToEmployees(eq(uuid), eq(organizationId))).willReturn(updatedRecords);
        given(organizationService.getOrganization(organizationId)).willReturn(organization);

        int totalUpdatedRecords = awardsService.giveDundieAwards(uuid, organizationId);

        assertThat(totalUpdatedRecords).isEqualTo(updatedRecords);

        verify(messageBroker).publishAwardOrganizationSuccessEvent(
                eq(uuid),
                any(Instant.class),
                eq(updatedRecords),
                eq(updatedRecords),
                eq(organization)
        );

        verify(employeeService).fetchEmployeeRollbackData(uuid, organizationId);
        verify(awardOperationLogService).createAwardOperationLog(eq(uuid), any(Instant.class), eq(rollbackData));
        verify(employeeService).addDundieAwardToEmployees(eq(uuid), eq(organizationId));
    }

    @Test
    void shouldPreventivelyBlockOrganizationId() {
        UUID uuid = UUID.randomUUID();
        long organizationId = 1L;

        awardsService.preventiveBlockOrganizationId(uuid, organizationId);

        verify(organizationService).block(uuid, organizationId);
    }

    @Test
    void shouldHandleSaveActivityAwardOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Event event = Event.createSaveActivityAwardOrganizationSuccessEvent(
                uuid,
                Instant.now(),
                10,
                Activity.builder().build(),
                organization
        );

        awardsService.handleSaveActivityAwardOrganizationSuccessEvent(event);

        verify(awardsCache).addAwards(10);
        verify(activityService).createActivity(event.toAddCacheActivity());
    }

    @Test
    void shouldHandleSaveActivityAwardOrganizationFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Activity rollbackActivity = Activity.builder()
                .occurredAt(Instant.now())
                .event("ACTIVITY ACK FAILURE - Will try Rollback for organization: Test Organization.")
                .build();

        Event event = Event.createSaveActivityAwardOrganizationFailureEvent(
                uuid,
                Instant.now(),
                10,
                rollbackActivity,
                organization
        );

        AwardOperationLog logRecord = AwardOperationLog.builder()
                .rollbackData("1,5|2,3|3,2")
                .build();

        given(awardOperationLogService.getAwardOperationLog(uuid)).willReturn(logRecord);
        given(appProperties.retryRollback()).willReturn(new AppProperties.RetryRollback(3, 1000));

        awardsService.handleSaveActivityAwardOrganizationFailureEvent(event);

        verify(activityService, times(2)).createActivity(event.toActivityRollback());

        verify(messageBroker).publishAwardOrganizationRollbackSuccessEvent(any(), any(), any(), any());

    }





    @Test
    void shouldHandleAwardOrganizationRollbackSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Event event = Event.createAwardOrganizationRollbackSuccessEvent(
                uuid,
                Instant.now(),
                10,
                Activity.builder().build(),
                organization
        );

        awardsService.handleAwardOrganizationRollbackSuccessEvent(event);

        verify(awardsCache).removeAwards(10);
        verify(activityService).createActivity(event.toRemoveCacheActivity());
    }

    @Test
    void shouldHandleAwardOrganizationRollbackRetryEvent() throws InterruptedException {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Event event = Event.createAwardOrganizationRollbackRetryEvent(
                uuid,
                Instant.now(),
                10,
                1,
                Activity.builder().build(),
                organization
        );

        given(appProperties.retryRollback()).willReturn(new AppProperties.RetryRollback(3, 1000));
        AwardOperationLog logRecord = AwardOperationLog.builder()
                .rollbackData("1,5|2,3|3,2")
                .build();

        given(awardOperationLogService.getAwardOperationLog(uuid)).willReturn(logRecord);
        awardsService.handleAwardOrganizationRollbackRetryEvent(event);

        verify(activityService).createActivity(event.toRetryRollback());
    }

    @Test
    void shouldHandleAwardOrganizationRollbackFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Activity rollbackFailureActivity = Activity.builder()
                .occurredAt(Instant.now())
                .event("Failure when trying to rollback organization: Test Organization.")
                .build();

        Event event = Event.createAwardOrganizationRollbackFailureEvent(
                uuid,
                Instant.now(),
                10,
                rollbackFailureActivity,
                organization
        );

        AwardOperationLog logRecord = AwardOperationLog.builder()
                .rollbackData("1,5|2,3|3,2")
                .build();

        given(awardOperationLogService.getAwardOperationLog(uuid)).willReturn(logRecord);

        awardsService.handleAwardOrganizationRollbackFailureEvent(event);

    }

}
