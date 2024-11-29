package com.ninjaone.dundie_awards.service;

import java.time.Instant;
import java.util.UUID;

public interface AwardOperationLogService {

	void createAwardOperationLog(UUID uuid, Instant occurredAt, String rollbackData);

	void cleanAwardOperationLog(UUID uuid);

}
