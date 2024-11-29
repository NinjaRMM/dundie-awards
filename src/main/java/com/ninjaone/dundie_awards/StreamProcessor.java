package com.ninjaone.dundie_awards;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.AwardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamProcessor {

    private final ActivityService activityService;
    private final AwardService awardService;

    @EventListener
    public void process(Event event) {
    	log.info("UUID: {} - process - StreamProcessing processing event: {}", event.uuid(), event.toJson());
        switch (event.eventType()) {
        	//If given award to organization where success then try to create Activity
            case AWARD_ORGANIZATION_SUCCESS_EVENT -> 
                activityService.handleAwardOrganizationSuccessEvent(event);
            case SAVE_ACTIVITY_AWARD_ORGANIZATION_SUCCESS_EVENT -> 
                awardService.handleSaveActivityAwardOrganizationSuccessEvent(event);
            case SAVE_ACTIVITY_AWARD_ORGANIZATION_RETRY_EVENT -> 
                activityService.handleSaveActivityAwardOrganizationRetryEvent(event);
            //Else start rollback process
            case SAVE_ACTIVITY_AWARD_ORGANIZATION_FAILURE_EVENT -> 
                awardService.handleSaveActivityAwardOrganizationFailureEvent(event);
            //Rollback events are handled at AwardService
            case AWARD_ORGANIZATION_ROLLBACK_SUCCESS_EVENT -> 
                awardService.handleAwardOrganizationRollbackSuccessEvent(event);
            case AWARD_ORGANIZATION_ROLLBACK_RETRY_EVENT -> 
            	awardService.handleAwardOrganizationRollbackRetryEvent(event);
            case AWARD_ORGANIZATION_ROLLBACK_FAILURE_EVENT -> 
            	awardService.handleAwardOrganizationRollbackFailureEvent(event);
            default -> 
                throw new UnsupportedOperationException("Unsupported event type: " + event.eventType());
        }
    }
}
