package com.ninjaone.dundie_awards.repository.performance;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.AwardOperationLog;
import com.ninjaone.dundie_awards.repository.AwardOperationLogRepository;

import lombok.extern.slf4j.Slf4j;


/*
 * Excellent performance:
 * Setup started for performance comparison...
 * Setup completed for performance comparison in 0,11 seconds
 * Retrieving completed for performance comparison in 0,00 seconds
 * Processing completed for performance comparison in 0,77 seconds
 * Parsed 3000000 entries
 */
@Disabled("This test is currently ignored. Test just to compare performances")
@DataJpaTest(showSql = false)
@Transactional
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AwardOperationLogRepositorySavePerformanceTest {

    @Autowired
    private AwardOperationLogRepository repository;

    @Test
    void compareQueryPerformance() {
    	
    	repository.deleteAll();
    	
    	String rollbackData = LongStream.rangeClosed(1, 3_000_000)
                .mapToObj(id -> id + "," + ThreadLocalRandom.current().nextInt(0, 13))
                .collect(Collectors.joining("|"));
    	
        long startSaving = System.currentTimeMillis();
        log.info("Setup started for performance comparison...");
        String uuid = UUID.randomUUID().toString();

        repository.save(AwardOperationLog.builder()
        		.uuid(uuid)
        		.rollbackData(rollbackData)
        		.build());
        repository.flush();
        long endSaving = System.currentTimeMillis();
        log.info("Setup completed for performance comparison in {} seconds", String.format("%.2f", (endSaving - startSaving) / 1000.0));
        
        long startRetrieving = System.currentTimeMillis();
        AwardOperationLog logRecord = repository.getReferenceById(uuid);
        String savedRollbackData = logRecord.getRollbackData();
        long endRetrieving = System.currentTimeMillis();
        log.info("Retrieving completed for performance comparison in {} seconds", String.format("%.2f", (endRetrieving - startRetrieving) / 1000.0));
        
        long startProcessing = System.currentTimeMillis();
        Set<AbstractMap.SimpleEntry<Long, Integer>> parsedData = Arrays.stream(savedRollbackData.split("\\|"))
                .map(record -> record.split(",", 2)) 
                .filter(fields -> fields.length == 2)
                .map(fields -> {
                    try {
                        Long id = Long.parseLong(fields[0].trim());
                        Integer value = Integer.parseInt(fields[1].trim());
                        return new AbstractMap.SimpleEntry<>(id, value);
                    } catch (NumberFormatException e) {
                        log.info("Invalid record: {}", String.join(",", fields));
                        return null; 
                    }
                })
                .filter(entry -> entry != null) 
                .collect(Collectors.toSet());
        long endProcessing = System.currentTimeMillis();
        log.info("Processing completed for performance comparison in {} seconds", String.format("%.2f", (endProcessing - startProcessing) / 1000.0));
        
        log.info("Parsed {} entries", parsedData.size());
        assert parsedData.size() == 3_000_000;

    }

}
