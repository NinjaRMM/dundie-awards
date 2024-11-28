package com.ninjaone.dundie_awards.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.service.AwardOperationLogService;
import com.ninjaone.dundie_awards.service.AwardService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import com.ninjaone.dundie_awards.service.OrganizationService;
import com.ninjaone.dundie_awards.util.BatchUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwardServiceImpl implements AwardService {

	
	private final AppProperties dundieProperties;
	private final EmployeeService employeeService;
	private final AwardOperationLogService awardOperationLogService;
	private final OrganizationService organizationService;
	private final AwardsCache awardsCache;
	
    @Override
    @Transactional
    public int giveDundieAwards(UUID uuid, long organizationId) {
        log.info("UUID: {} - giveDundieAwards - Processing awards for Organization ID: {}", uuid, organizationId);
        
        String rollbackData = employeeService.fetchEmployeeRollbackData(uuid, organizationId);
        awardOperationLogService.createAwardOperationLog(uuid, rollbackData);
        int totalUpdatedRecords = employeeService.addDundieAwardToEmployees(uuid, organizationId);
        organizationService.block(uuid, organizationId);
        awardsCache.addAwards(totalUpdatedRecords);

//        Giveup on batch for performance improvement
//        List<List<Long>> batches = chunkIntoBatches(employeeIds);
//        log.info("UUID: {} - Split employees into {} batches with a batch size of {}", uuid, batches.size(), dundieProperties.batchSize());

        
        log.info("UUID: {} - giveDundieAwards - Successfully processed awards for Organization ID: {}. Total updated records: {}", uuid, organizationId, totalUpdatedRecords);
        return totalUpdatedRecords;
    }
    
    private List<List<Long>> chunkIntoBatches(List<Long> ids) {
    	int batchSize = dundieProperties.batchSize();
    	return BatchUtil.chunkIntoBatches(ids, batchSize);
    }
}
