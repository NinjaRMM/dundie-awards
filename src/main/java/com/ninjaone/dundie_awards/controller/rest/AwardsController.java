package com.ninjaone.dundie_awards.controller.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.validation.BlockedOrganizationId;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Awards API", description = "API for managing awards")
@RequestMapping
public interface AwardsController {

    @PostMapping("/give-dundie-awards/{organizationId}")
    public void giveOrganizationDundieAwards(@Valid @BlockedOrganizationId @PathVariable("organizationId") long organizationId);

}