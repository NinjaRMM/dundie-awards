package com.ninjaone.dundie_awards.service;

import java.util.List;

import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.event.Event;

public interface ActivityService {

	List<ActivityDto> getAllActivities();

	public void handleAwardOrganizationSuccessEvent(Event event);
	public void handleSaveActivityAwardOrganizationRetryEvent(Event event);


}