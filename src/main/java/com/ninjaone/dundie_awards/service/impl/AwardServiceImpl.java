package com.ninjaone.dundie_awards.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.AwardOperationLogService;
import com.ninjaone.dundie_awards.service.AwardService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import com.ninjaone.dundie_awards.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwardServiceImpl implements AwardService {

	
	private final EmployeeService employeeService;
	private final AwardOperationLogService awardOperationLogService;
	private final OrganizationService organizationService;
	private final ActivityService activityService;
	private final AwardsCache awardsCache;
	private final MessageBroker messageBroker;
	
    @Override
    @Transactional
    public int giveDundieAwards(UUID uuid, long organizationId) {
        log.info("UUID: {} - giveDundieAwards - Processing awards for Organization ID: {}", uuid, organizationId);
        Instant occurredAt = Instant.now();
        String rollbackData = employeeService.fetchEmployeeRollbackData(uuid, organizationId);
        awardOperationLogService.createAwardOperationLog(uuid,occurredAt, rollbackData);
        int totalUpdatedRecords = employeeService.addDundieAwardToEmployees(uuid, organizationId);
        organizationService.block(uuid, organizationId);
        messageBroker.publishAwardOrganizationSuccessEvent(
        				uuid, 
        				occurredAt , 
        				totalUpdatedRecords, 
        				totalUpdatedRecords, 
        				organizationService.getOrganization(organizationId));

        log.info("UUID: {} - giveDundieAwards - Successfully processed awards for Organization ID: {}. Total updated records: {}", uuid, organizationId, totalUpdatedRecords);
        return totalUpdatedRecords;
    }
    
    @Override
    public void handleSaveActivityAwardOrganizationSuccessEvent(Event event) {
        log.info("UUID: {} - handleSaveActivityAwardOrganizationSuccessEvent - Handle event: {}", event.uuid(), event.toJson());
        awardsCache.addAwards(event.totalAwards());
        activityService.createActivity(event.toUpdateCacheActivity());
        log.info("UUID: {} - handleSaveActivityAwardOrganizationSuccessEvent - Handled event: {}", event.uuid(), event.toJson());
    }

    @Override
    public void handleSaveActivityAwardOrganizationFailureEvent(Event event) {
        log.info("UUID: {} - handleSaveActivityAwardOrganizationFailureEvent - Handle event: {}", event.uuid(), event.toJson());
        log.info("UUID: {} - handleSaveActivityAwardOrganizationFailureEvent - Handled event: {}", event.uuid(), event.toJson());
    }

    @Override
    public void handleAwardOrganizationRollbackSuccessEvent(Event event) {
        log.info("UUID: {} - handleAwardOrganizationRollbackSuccessEvent - Handle event: {}", event.uuid(), event.toJson());
        log.info("UUID: {} - handleAwardOrganizationRollbackSuccessEvent - Handled event: {}", event.uuid(), event.toJson());
    }

    @Override
    public void handleAwardOrganizationRollbackRetryEvent(Event event) {
        log.info("UUID: {} - handleAwardOrganizationRollbackRetryEvent - Handle event: {}", event.uuid(), event.toJson());
        log.info("UUID: {} - handleAwardOrganizationRollbackRetryEvent - Handled event: {}", event.uuid(), event.toJson());
    }

    @Override
    public void handleAwardOrganizationRollbackFailureEvent(Event event) {
        log.info("UUID: {} - handleAwardOrganizationRollbackFailureEvent - Handle event: {}", event.uuid(), event.toJson());
        log.info("UUID: {} - handleAwardOrganizationRollbackFailureEvent - Handled event: {}", event.uuid(), event.toJson());
    }
}
