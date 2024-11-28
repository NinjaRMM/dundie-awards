package com.ninjaone.dundie_awards.validation;

import com.ninjaone.dundie_awards.service.OrganizationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BlockedOrganizationValidator implements ConstraintValidator<BlockedOrganizationId, Long> {

    private final OrganizationService organizationService;

    public BlockedOrganizationValidator(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    public boolean isValid(Long organizationId, ConstraintValidatorContext context) {
        if (organizationId == null) {
            return true; 
        }
        return !organizationService.isBlocked(organizationId);
    }
}
