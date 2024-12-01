package com.ninjaone.dundie_awards.service;

import java.time.Instant;
import java.util.UUID;

import com.ninjaone.dundie_awards.model.AwardOperationLog;

public interface AwardOperationLogService {

	AwardOperationLog getAwardOperationLog(UUID uuid);

	void createAwardOperationLog(UUID uuid, Instant occurredAt, String rollbackData);

	void cleanAwardOperationLog(UUID uuid);

}
