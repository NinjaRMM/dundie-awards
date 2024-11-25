package com.ninjaone.dundie_awards.service.impl;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.organizationNotFoundException;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;

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

    private Organization findOrganizationById(Long id) {
        log.info("findOrganizationById - Searching for organization in the repository with ID: {}", id);
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("findOrganizationById - Organization with ID: {} not found", id);
                    return organizationNotFoundException.apply(id);
                });
        log.info("findOrganizationById - Found organization with ID: {}: {}", id, organization);
        return organization;
    }
}
