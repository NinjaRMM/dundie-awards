package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;
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
    private AwardsCache awardsCache;

    @Mock
    private MessageBroker messageBroker;

    @Mock
    private ActivityService activityService;

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
        Organization organization = new Organization();

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
        verify(organizationService).block(uuid, organizationId);
    }

    @Test
    void shouldHandleSaveActivityAwardOrganizationSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Activity activity = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization")
                .build();
        Event event = Event.createSaveActivityAwardOrganizationSuccessEvent(
                uuid,
                Instant.now(),
                10,
                activity,
                organization
        );

        given(activityService.createActivity(any(Activity.class))).willReturn(activity);

        awardsService.handleSaveActivityAwardOrganizationSuccessEvent(event);

        verify(awardsCache).addAwards(eq(10));
        verify(activityService).createActivity(eq(activity));
    }


    @Test
    void shouldHandleSaveActivityAwardOrganizationFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Organization organization = Organization.builder().id(1L).name("Test Organization").build();
        Activity activity = Activity.builder()
                .occurredAt(Instant.now())
                .event("Awards organization")
                .build();
        Event event = Event.createSaveActivityAwardOrganizationFailureEvent(
                uuid,
                Instant.now(),
                10,
                activity,
                organization
        );

        awardsService.handleSaveActivityAwardOrganizationFailureEvent(event);

        // Verificar se há alguma lógica ou comportamento esperado em failure handling, se necessário
    }

    @Test
    void shouldHandleAwardOrganizationRollbackSuccessEvent() {
        UUID uuid = UUID.randomUUID();
        Event event = Event.createAwardOrganizationRollbackSuccessEvent(
                uuid,
                Instant.now(),
                10,
                10
        );

        awardsService.handleAwardOrganizationRollbackSuccessEvent(event);
    }

    @Test
    void shouldHandleAwardOrganizationRollbackRetryEvent() {
        UUID uuid = UUID.randomUUID();
        Event event = Event.createAwardOrganizationRollbackRetryEvent(
                uuid,
                Instant.now(),
                1
        );

        awardsService.handleAwardOrganizationRollbackRetryEvent(event);
    }

    @Test
    void shouldHandleAwardOrganizationRollbackFailureEvent() {
        UUID uuid = UUID.randomUUID();
        Event event = Event.createAwardOrganizationRollbackFailureEvent(
                uuid,
                Instant.now()
        );

        awardsService.handleAwardOrganizationRollbackFailureEvent(event);
    }

}
