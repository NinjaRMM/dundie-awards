package com.ninjaone.dundie_awards.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.AwardOperationLog;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.AwardOperationLogService;
import com.ninjaone.dundie_awards.service.AwardService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import com.ninjaone.dundie_awards.service.OrganizationService;
import com.ninjaone.dundie_awards.util.RollbackDataComparator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
	private final AppProperties appProperties;
	
	//optimistic lock
	@Override
	@Transactional
	public void preventiveBlockOrganizationId(UUID uuid, long organizationId) {
		organizationService.block(uuid, organizationId);
	}
	
    @Override
    @Transactional
    public int giveDundieAwards(UUID uuid, long organizationId) {
        log.info("UUID: {} - giveDundieAwards - Processing awards for Organization ID: {}", uuid, organizationId);
        Instant occurredAt = Instant.now();
        String rollbackData = employeeService.fetchEmployeeRollbackData(uuid, organizationId);
        awardOperationLogService.createAwardOperationLog(uuid,occurredAt, rollbackData);
        int totalUpdatedRecords = employeeService.addDundieAwardToEmployees(uuid, organizationId);
        //moving this to a separated Transaction to avoid concurrency
        //organizationService.block(uuid, organizationId);
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
        activityService.createActivity(event.toAddCacheActivity());
        log.info("UUID: {} - handleSaveActivityAwardOrganizationSuccessEvent - Handled event: {}", event.uuid(), event.toJson());
    }
    
    /*
     * SQUANCHY			success in roll-back (at first try, without retry)
     * GARFIELD 		successes in activity save (no roll-back needed)
     * FRAJOLA 			success in roll-back (at first try, without retry) 
     * TOM 				fails but recovers while retrying to roll-back
     * HELLO_KITTY 		fail in all
     */
    //just to simulate some behavior of failures to simulate the retries or roll back
    private void simulateTestBehavior(Organization organization, Event event) {
    	if (Organization.TOM.equals(organization) && (event.calculatedAttempt() % 2 == 0)) {
    		throw new RuntimeException("Will recover from failure");
    	}
        if (Organization.HELLO_KITTY.equals(organization)) {
            throw new RuntimeException("Will fail until stop");
        }
    }
    
    @Transactional
    private void rollback(UUID uuid, Integer totalAwards, Activity activity, Organization organization, Event event) {
    	log.info("UUID: {} - rollback - Starting rollback for activity: {}", uuid, activity);
    	
    	if (appProperties.enableTestBehavior()) {
    		simulateTestBehavior(organization, event);
    	}
    	
    	//retrieve rollback data
    	AwardOperationLog logRecord = awardOperationLogService.getAwardOperationLog(uuid);
    	if (logRecord == null) {
    	    throw new IllegalStateException("No AwardOperationLog found for UUID: " + uuid);
    	}
    	String rollbackData = logRecord.getRollbackData();
    	//rollback by decreasing
    	employeeService.removeDundieAwardToEmployees(uuid, organization.getId());
    	//get data to compare after decrease
    	String comparisonData = employeeService.fetchEmployeeComparisonData(uuid, organization.getId());
    	//treat each unexpected difference
    	/*
    	 * This shall not occurs, but to prevent cases where some operation was executed in the middle of rollback
    	 * We can compare the changed data with the log, and log the unexpected change
    	 */
    	List<RollbackDataComparator.Diff> unexpectedDifferences = RollbackDataComparator.findDifferences(rollbackData, comparisonData);
        unexpectedDifferences.forEach(diff -> {
            log.warn("UUID: {} - Unexpected difference detected for Organization ID: {}. Employee ID: {}, Original Dundie Awards: {}, Unexpected Dundie Awards: {}",
                uuid, organization.getId(), diff.id(), diff.originalValue(), diff.unexpectedValue());
            //Only logs it. I really think that if another one changes, we can only decrease by one, and its ok
            //We do not need to reset the value to prevent data lost(in this case, the changes done by other concurrency operations)
            //employeeService.updateDundieAwards(uuid,diff.id(), diff.originalValue());
        });
        
    	awardOperationLogService.cleanAwardOperationLog(uuid);
    	organizationService.unblock(uuid, organization);
    	
    	activityService.createActivity(event.toRollbackSuccessActivity());
    	messageBroker.publishAwardOrganizationRollbackSuccessEvent(uuid,activity,totalAwards, organization);
    	
    }

    @Override
    public void handleSaveActivityAwardOrganizationFailureEvent(Event event) {
        log.info("UUID: {} - handleSaveActivityAwardOrganizationFailureEvent - Handle event: {}", event.uuid(), event.toJson());
        activityService.createActivity(event.toActivityRollback());
        processRollback(event);
        log.info("UUID: {} - handleSaveActivityAwardOrganizationFailureEvent - Handled event: {}", event.uuid(), event.toJson());
    }
    
    @Override
    @SneakyThrows
    public void handleAwardOrganizationRollbackRetryEvent(Event event) {
    	log.info("UUID: {} - handleAwardOrganizationRollbackRetryEvent - Handle event: {}", event.uuid(), event.toJson());
    	
    	//delay await time: back-off * attempt before trying again
        int backoffDelayMs = appProperties.retryRollback().backoffDelayMs() * event.calculatedAttempt();
        log.info("UUID: {} - handleAwardOrganizationRollbackRetryEvent - Applying backoff: {} ms for attempt: {}", event.uuid(), backoffDelayMs, event.calculatedAttempt());
        Thread.sleep(backoffDelayMs);

        processRollback(event);
    	log.info("UUID: {} - handleAwardOrganizationRollbackRetryEvent - Handled event: {}", event.uuid(), event.toJson());
    }

    //apply the try catch logic
    private void processRollback(Event event) {
    	
    	Activity activity=event.activity();
    	Organization organization = event.organization();
    	Integer totalAwards = event.totalAwards();
    	UUID uuid = event.uuid();
    	
        try {
        	rollback(event.uuid(),totalAwards, activity, organization, event);
        } catch (Exception e) {
        	if (appProperties.enableTestBehavior()) {
        		log.error("UUID: {} - processActivityCreation - Exception occurred: {}",uuid, e.getLocalizedMessage());
        	}else {
        		log.error("UUID: {} - processActivityCreation - Exception occurred:",uuid, e);
        	}
            failureAtRollback(event, activity, organization,totalAwards);
        }
    }
    
    //apply the failure retry logic
    private void failureAtRollback(Event event, Activity activity, Organization organization, int totalAwards) {
        if (event.calculatedAttempt() < appProperties.retryRollback().maxAttempts()) {
            log.info("UUID: {} - failureAtRollback - Publishing RETRY attempt: {}", event.uuid(), event.calculatedAttempt()+1);
            activityService.createActivity(event.toRetryRollback());
            messageBroker.publishAwardOrganizationRollbackRetryEvent(event.uuid(), totalAwards, event.calculatedAttempt()+1, activity, organization);
        } else {
            log.error("UUID: {} - failureAtRollback - Publishing FAILURE event.", event.uuid());
            activityService.createActivity(event.toFailureRollback());
            messageBroker.publishAwardOrganizationRollbackFailureEvent(event.uuid(), totalAwards, activity, organization);
        }
    }
    
    @Override
    public void handleAwardOrganizationRollbackSuccessEvent(Event event) {
        log.info("UUID: {} - handleAwardOrganizationRollbackSuccessEvent - Handle event: {}", event.uuid(), event.toJson());
        awardsCache.removeAwards(event.totalAwards());
        activityService.createActivity(event.toRemoveCacheActivity());
        log.info("UUID: {} - handleAwardOrganizationRollbackSuccessEvent - Handled event: {}", event.uuid(), event.toJson());
    }


    @Override
    public void handleAwardOrganizationRollbackFailureEvent(Event event) {
        log.info("UUID: {} - handleAwardOrganizationRollbackFailureEvent - Handle event: {}", event.uuid(), event.toJson());
        log.info("UUID: {} - handleAwardOrganizationRollbackFailureEvent - Handled event: {}", event.uuid(), event.toJson());
    }
    
}
