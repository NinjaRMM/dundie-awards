package com.ninjaone.dundie_awards.controller.rest;

import java.util.UUID;

import org.springframework.web.bind.annotation.RestController;

import com.ninjaone.dundie_awards.service.AwardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
public class AwardsControllerImpl implements AwardsController {

	private final AwardService awardService;

    @Override
    public void giveOrganizationDundieAwards(long organizationId) {
    	//UUID to track request 
    	UUID uuid = UUID.randomUUID();
        log.info("UUID: {} - POST /give-dundie-awards/{} - give Dundie Award to Organization with ID: {}", uuid, organizationId, organizationId);

        int totalUpdatedRecords = awardService.giveDundieAwards(uuid, organizationId);
        
    	log.info("UUID: {} - POST /give-dundie-awards/{} - Successfully updated employees: {}.", uuid, organizationId, totalUpdatedRecords);
    }


}