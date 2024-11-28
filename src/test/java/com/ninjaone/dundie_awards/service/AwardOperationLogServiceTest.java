package com.ninjaone.dundie_awards.service;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.repository.AwardOperationLogRepository;
import com.ninjaone.dundie_awards.service.impl.AwardOperationLogServiceImpl;

class AwardOperationLogServiceTest {

    @Mock
    private AwardOperationLogRepository awardOperationLogRepository;

    @InjectMocks
    private AwardOperationLogServiceImpl awardOperationLogService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateAwardOperationLogTests {

        @Test
        void shouldCreateAwardOperationLog() {
            UUID uuid = UUID.randomUUID();
            String rollbackData = "1,8|2,9|3,2";

            awardOperationLogService.createAwardOperationLog(uuid,Instant.now(), rollbackData);

            then(awardOperationLogRepository).should(times(1))
                    .save(org.mockito.ArgumentMatchers.argThat(log -> 
                            log.getUuid().equals(uuid.toString()) &&
                            log.getRollbackData().equals(rollbackData) &&
                            log.getOccurredAt() != null
                    ));
        }
    }
}
