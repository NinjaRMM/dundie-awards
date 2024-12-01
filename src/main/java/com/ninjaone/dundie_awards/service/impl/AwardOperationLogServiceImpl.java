package com.ninjaone.dundie_awards.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.AwardOperationLog;
import com.ninjaone.dundie_awards.repository.AwardOperationLogRepository;
import com.ninjaone.dundie_awards.service.AwardOperationLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwardOperationLogServiceImpl implements AwardOperationLogService {

    private final AwardOperationLogRepository awardOperationLogRepository;

    @Override
    @Transactional
    public AwardOperationLog getAwardOperationLog(UUID uuid) {
    	log.info("UUID: {} - getAwardOperationLog - Retrieving AwardOperationLog", uuid);
    	AwardOperationLog awardOperationLog = awardOperationLogRepository.getReferenceById(uuid.toString());
    	//load Lob data
    	awardOperationLog.getRollbackData();
    	log.info("UUID: {} - getAwardOperationLog - Retrieving createAwardOperationLog", uuid);
    	return awardOperationLog;
    }

    @Override
	public void createAwardOperationLog(UUID uuid,Instant occurredAt, String rollbackData) {
        log.info("UUID: {} - createAwardOperationLog - Creating AwardOperationLog", uuid);
        awardOperationLogRepository.save(
        		AwardOperationLog.builder()
        		.uuid(uuid.toString())
        		.occurredAt(occurredAt)
        		.rollbackData(rollbackData)
        		.build());
        log.info("UUID: {} - createAwardOperationLog - Created createAwardOperationLog", uuid);
    }

	@Override
	public void cleanAwardOperationLog(UUID uuid) {
		log.info("UUID: {} - cleanAwardOperationLog - Cleaning AwardOperationLog", uuid);
		awardOperationLogRepository.deleteById(uuid.toString());
		log.info("UUID: {} - cleanAwardOperationLog - Cleaned AwardOperationLog", uuid);
	}
}
