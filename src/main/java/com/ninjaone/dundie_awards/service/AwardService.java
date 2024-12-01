package com.ninjaone.dundie_awards.service;

import java.util.UUID;

import com.ninjaone.dundie_awards.event.Event;

public interface AwardService {

	void preventiveBlockOrganizationId(UUID uuid, long organizationId);
    int giveDundieAwards(UUID requestId, long organizationId);
    public void handleSaveActivityAwardOrganizationSuccessEvent(Event event);
    public void handleSaveActivityAwardOrganizationFailureEvent(Event event);
    public void handleAwardOrganizationRollbackSuccessEvent(Event event);
    public void handleAwardOrganizationRollbackRetryEvent(Event event);
    public void handleAwardOrganizationRollbackFailureEvent(Event event);

}
