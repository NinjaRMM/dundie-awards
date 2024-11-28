package com.ninjaone.dundie_awards.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
}
