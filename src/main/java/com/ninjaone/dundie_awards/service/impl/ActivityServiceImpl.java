package com.ninjaone.dundie_awards.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.event.EventType;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.AwardOperationLogService;
import com.ninjaone.dundie_awards.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final MessageBroker messageBroker;
    private final AppProperties appProperties;
	private final AwardOperationLogService awardOperationLogService;
	private final OrganizationService organizationService;

    @Override
	public List<ActivityDto> getAllActivities() {
        log.debug("getAllActivities - Fetching all activities from the database.");
        List<ActivityDto> activities = activityRepository.findAll(Sort.by(Sort.Direction.DESC, "occurredAt"))
                .stream()
                .map(ActivityDto::toDto)
                .toList();
        log.debug("getAllActivities - Fetched {} activities.", activities.size());
        return activities;
    }
    
    @Override
    @Transactional
	public Activity createActivity(Activity activity) {
        log.info("createActivity - Creating activity: {}", activity);
        Activity savedActivity = activityRepository.save(activity);
        log.info("createActivity - Created activity: {}", activity);
        return savedActivity;
    }
    
    
    /*
     * GARFIELD 		fails but recovers while retrying to save activity 
     * FRAJOLA 			fail in all 
     * TOM 				fail in all
     * HELLO_KITTY 		fail in all
     */
    //just to simulate some behavior of failures to simulate the retries or roll back
    private void simulateTestBehavior(Organization organization, Event event) {
        if (Organization.GARFIELD.equals(organization) && (event.calculatedAttempt() % 2 == 0)) {
            throw new RuntimeException("Will recover from failure");
        }
        if (Organization.SQUANCHY.equals(organization) ||
        		Organization.FRAJOLA.equals(organization) ||
        		Organization.TOM.equals(organization) ||
        		Organization.HELLO_KITTY.equals(organization)) {
        	throw new RuntimeException("Will fail until stop");
        }
    }

    @Transactional
    private void finalizeActivityTransaction(UUID uuid, Integer totalAwards, Activity activity, Organization organization, Event event) {
    	log.info("UUID: {} - finalizeActivityTransaction - Starting transaction for activity: {}", uuid, activity);
    	
    	if (appProperties.enableTestBehavior()) {
    		simulateTestBehavior(organization, event);
    	}
    	
    	log.info("UUID: {} - finalizeActivityTransaction - Creating activity: {}", uuid, activity);
    	Activity savedActivity = activityRepository.save(activity);
    	log.info("UUID: {} - finalizeActivityTransaction - Created activity: {}", uuid, savedActivity);
    	awardOperationLogService.cleanAwardOperationLog(uuid);
    	organizationService.unblock(uuid, organization);
    	messageBroker.publishSaveActivityAwardOrganizationSuccessEvent(uuid,activity,totalAwards, organization);
    	
    }

    @Override
    public void handleAwardOrganizationSuccessEvent(Event event) {
        log.info("UUID: {} - handleAwardOrganizationSuccessEvent - Handle event: {}", event.uuid(), event.toJson());
        createActivity(event.toActivity());
        processActivityCreation(event);
        log.info("UUID: {} - handleAwardOrganizationSuccessEvent - Handled event: {}", event.uuid(), event.toJson());
    }

    @Override
    @SneakyThrows
    public void handleSaveActivityAwardOrganizationRetryEvent(Event event) {
        log.info("UUID: {} - handleSaveActivityAwardOrganizationRetryEvent - Handle event: {}", event.uuid(), event.toJson());
        
        //delay await time: back-off * attempt before trying again
        int backoffDelayMs = appProperties.retrySaveActivity().backoffDelayMs() * event.calculatedAttempt();
        log.info("UUID: {} - handleSaveActivityAwardOrganizationRetryEvent - Applying backoff: {} ms for attempt: {}", event.uuid(), backoffDelayMs, event.calculatedAttempt());
        Thread.sleep(backoffDelayMs);
        
        processActivityCreation(event);
        log.info("UUID: {} - handleSaveActivityAwardOrganizationRetryEvent - Handled event: {}", event.uuid(), event.toJson());
    }
    
    //apply the try catch logic
    private void processActivityCreation(Event event) {
    	
    	Activity activity=extractActivityFromEvent(event);
    	Organization organization = event.organization();
    	Integer totalAwards = event.totalAwards();
    	UUID uuid = event.uuid();
    	
        try {
        	finalizeActivityTransaction(event.uuid(),totalAwards, activity, organization, event);
        } catch (Exception e) {
        	if (appProperties.enableTestBehavior()) {
        		log.error("UUID: {} - processActivityCreation - Exception occurred: {}",uuid, e.getLocalizedMessage());
        	}else {
        		log.error("UUID: {} - processActivityCreation - Exception occurred:",uuid, e);
        	}
            failureAtActivityCreation(event, activity, organization,totalAwards);
        }
    }

    //apply the failure retry logic
    private void failureAtActivityCreation(Event event, Activity activity, Organization organization, int totalAwards) {
        if (event.calculatedAttempt() < appProperties.retrySaveActivity().maxAttempts()) {
            log.info("UUID: {} - failureAtActivityCreation - Publishing RETRY attempt: {}", event.uuid(), event.calculatedAttempt()+1);
            createActivity(event.toRetryActivity());
            messageBroker.publishSaveActivityAwardOrganizationRetryEvent(event.uuid(), totalAwards, event.calculatedAttempt()+1, activity, organization);
        } else {
            log.error("UUID: {} - failureAtActivityCreation - Publishing FAILURE event.", event.uuid());
            createActivity(event.toFailureActivity());
            messageBroker.publishSaveActivityAwardOrganizationFailureEvent(event.uuid(), totalAwards, activity, organization);
        }
    }
    
    //retrieve Activity from retry event or create one
    private Activity extractActivityFromEvent(Event event) {
        if (EventType.AWARD_ORGANIZATION_SUCCESS_EVENT.equals(event.eventType())) {
            return event.toAckActivity();
        } else if (EventType.SAVE_ACTIVITY_AWARD_ORGANIZATION_RETRY_EVENT.equals(event.eventType())) {
            return event.activity().toBuilder().occurredAt(Instant.now()).build(); //update time
        } else {
            throw new UnsupportedOperationException("Unsupported event type: " + event.eventType());
        }
    }

    
}
