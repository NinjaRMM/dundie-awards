package com.ninjaone.dundie_awards.service.impl;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.organizationNotFoundException;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ninjaone.dundie_awards.dto.OrganizationDto;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    
    @Override
	public List<OrganizationDto> getAllOrganizations() {
        log.debug("getAllOrganizations - Fetching all organizations.");
        List<OrganizationDto> organizations = organizationRepository.findAll()
                .stream()
                .map(OrganizationDto::toDto)
                .collect(Collectors.toList());
        log.debug("getAllOrganizations - Fetched {} organizations.", organizations.size());
        return organizations;
    }
    
    @Override
	public boolean exists(Long id) {
        log.info("exists - Checking if organization exists with ID: {}", id);
        boolean exists = ofNullable(id)
                .map(organizationRepository::existsById)
                .orElse(false);
        log.info("exists - Organization with ID: {} exists: {}", id, exists);
        return exists;
    }

    @Override
	public Organization getOrganization(Long id) {
        log.info("getOrganization - Fetching organization with ID: {}", id);
        Organization organization = ofNullable(id)
                .map(this::findOrganizationById)
                .orElse(null);
        log.info("getOrganization - Fetched organization with ID: {}: {}", id, organization);
        return organization;
    }
    
    @Override
    public boolean isBlocked(Long id) {
        log.info("isBlocked - Checking if organization with ID: {} is blocked", id);
        boolean blocked = ofNullable(id)
                .map(this::findOrganizationById)
                .map(Organization::isBlocked)
                .orElse(false);
        log.info("isBlocked - Organization with ID: {} is blocked: {}", id, blocked);
        return blocked;
    }
    
    @Override
    public Organization block(UUID uuid, Long id) {
        log.info("UUID: {} - block - Blocking organization with ID: {} is blocked", uuid, id);
        Organization updatedOrganization = organizationRepository.save(
        		findOrganizationById(id).toBuilder()
        		.blocked(true)
        		.blockedBy(uuid.toString())
        		.build());
        log.info("UUID: {} - block - Blocked organization with ID : {} : {}", uuid, id, updatedOrganization);
		return updatedOrganization;
    }

    @Override
    public Organization unblock(UUID uuid, Organization organization) {
    	log.info("UUID: {} - unblock - Unblocking organization with ID: {}", uuid, organization.getId());
    	Organization updatedOrganization = organizationRepository.save(
    			organization.toBuilder()
    			.blocked(false)
    			.blockedBy(null)
    			.build());
    	log.info("UUID: {} - unblock - Unblocked organization with ID : {} : {}", uuid, organization.getId(), updatedOrganization);
    	return updatedOrganization;
    }

    private Organization findOrganizationById(Long id) {
        log.debug("findOrganizationById - Searching for organization in the repository with ID: {}", id);
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("findOrganizationById - Organization with ID: {} not found", id);
                    return organizationNotFoundException.apply(id);
                });
        log.debug("findOrganizationById - Found organization with ID: {}: {}", id, organization);
        return organization;
    }
}
