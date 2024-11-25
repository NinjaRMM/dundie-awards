package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.organizationNotFoundException;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;

import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {
	
	private final OrganizationRepository organizationRepository;
	
	public boolean exists(Long id) {
	    return ofNullable(id)
	        .map(organizationRepository::existsById)
	        .orElse(false);
	}

	public Organization getOrganization(Long id) {
		return ofNullable(id)
		        .map(this::findOrganizationById)
		        .orElse(null);
    }
	
	private Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> organizationNotFoundException.apply(id));
    }

}
