package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.invalidIdException;
import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.organizationNotValidException;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;

import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

@Service
public class OrganizationService {
	
	private final OrganizationRepository organizationRepository;

	public OrganizationService(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}
	
	public void ensureValidOrganization(Long id) {
	    ofNullable(id)
	        .filter(organizationRepository::existsById)
	        .orElseThrow(() -> id == null 
	            ? invalidIdException.get()
	            : organizationNotValidException.apply(id));
	}
	
	
	public Organization getValidOrganization(Long id) {
		return ofNullable(id)
				.map(validId -> organizationRepository.findById(validId)
		                .orElseThrow(() -> organizationNotValidException.apply(id)))
				.orElseThrow(()->invalidIdException.get());
	}

}
