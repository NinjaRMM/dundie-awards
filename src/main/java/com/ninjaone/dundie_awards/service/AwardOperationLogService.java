package com.ninjaone.dundie_awards.service;

import java.util.UUID;

public interface AwardOperationLogService {

	void createAwardOperationLog(UUID uuid, String rollbackData);

}
