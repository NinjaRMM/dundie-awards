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

import com.ninjaone.dundie_awards.model.AwardOperationLog;
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
    
    @Nested
    class CleanAwardOperationLogTests {

        @Test
        void shouldCleanAwardOperationLog() {
            UUID uuid = UUID.randomUUID();

            awardOperationLogService.cleanAwardOperationLog(uuid);

            then(awardOperationLogRepository).should(times(1)).deleteById(uuid.toString());
        }
    }
    
    @Nested
    class GetAwardOperationLogTests {

        @Test
        void shouldRetrieveAwardOperationLog() {
            UUID uuid = UUID.randomUUID();
            String rollbackData = "1,8|2,9|3,2";
            Instant occurredAt = Instant.now();

            AwardOperationLog mockLog = AwardOperationLog.builder()
                    .uuid(uuid.toString())
                    .rollbackData(rollbackData)
                    .occurredAt(occurredAt)
                    .build();
            org.mockito.BDDMockito.given(awardOperationLogRepository.getReferenceById(uuid.toString()))
                    .willReturn(mockLog);

            AwardOperationLog result = awardOperationLogService.getAwardOperationLog(uuid);

            // Verify the behavior and result
            org.assertj.core.api.Assertions.assertThat(result).isNotNull();
            org.assertj.core.api.Assertions.assertThat(result.getUuid()).isEqualTo(uuid.toString());
            org.assertj.core.api.Assertions.assertThat(result.getRollbackData()).isEqualTo(rollbackData);
            org.assertj.core.api.Assertions.assertThat(result.getOccurredAt()).isEqualTo(occurredAt);

            org.mockito.BDDMockito.then(awardOperationLogRepository).should(times(1))
                    .getReferenceById(uuid.toString());
        }
    }

}
