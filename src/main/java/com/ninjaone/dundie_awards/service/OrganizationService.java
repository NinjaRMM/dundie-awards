package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.model.Organization;

public interface OrganizationService {

	boolean exists(Long id);

	Organization getOrganization(Long id);

}