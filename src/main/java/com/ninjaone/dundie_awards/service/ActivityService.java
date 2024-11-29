package com.ninjaone.dundie_awards.service;

import java.util.List;

import com.ninjaone.dundie_awards.dto.ActivityDto;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Activity;

public interface ActivityService {

	List<ActivityDto> getAllActivities();

	Activity createActivity(Activity activity);
	
	public void handleAwardOrganizationSuccessEvent(Event event);
	public void handleSaveActivityAwardOrganizationRetryEvent(Event event);



}