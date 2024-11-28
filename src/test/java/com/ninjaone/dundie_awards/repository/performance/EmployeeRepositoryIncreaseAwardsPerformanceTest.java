package com.ninjaone.dundie_awards.repository.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.util.BatchUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Native ~10sec
 * HQL could not perform
 * Batch 0.7sec per 1kbatch => 35min 
 */
@Disabled("Performance tests are currently ignored")
@DataJpaTest(showSql = false)
@Transactional
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeRepositoryIncreaseAwardsPerformanceTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private List<Long> employeeIds;
    private Organization organization;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
        organizationRepository.deleteAll();
        
        long start = System.currentTimeMillis();
        log.info("Setup started for performance comparison...");

        organization = Organization.builder().id(1L).build();
        organizationRepository.save(organization);
        organizationRepository.flush();

        employeeIds = LongStream.rangeClosed(1, 3_000_000).boxed().toList();

        List<Employee> employees = employeeIds.stream()
                .map(id -> Employee.builder()
                        .id(id)
                        .dundieAwards(0)
                        .organization(organization)
                        .build())
                .toList();

        employeeRepository.saveAll(employees);
        employeeRepository.flush();

        long end = System.currentTimeMillis();
        log.info("Setup completed for performance comparison in {} seconds", String.format("%.2f", (end - start) / 1000.0));
    }

    @Test
    void testIncreaseAwardsPerformance() {
    	
    	log.info("Comparing performance of different methods to increase awards...");
    	
        long nativeStart = System.currentTimeMillis();
        log.info("Native Query started...");
        int totalUpdatedRecordsNative = employeeRepository.increaseAwardsToEmployeesNative(organization.getId());
        long nativeEnd = System.currentTimeMillis();
        log.info("Native Query completed in {} seconds", String.format("%.2f", (nativeEnd - nativeStart) / 1000.0));
        
        long hqlStart = System.currentTimeMillis();
        log.info("HQL Query started...");
        int totalUpdatedRecordsHql = employeeRepository.increaseAwardsToEmployees(organization.getId());
        long hqlEnd = System.currentTimeMillis();
        log.info("HQL Query completed in {} seconds", String.format("%.2f", (hqlEnd - hqlStart) / 1000.0));
        
        log.info("Batch Processing started...");
        long batchStart = System.currentTimeMillis();
        List<List<Long>> batches = BatchUtil.chunkIntoBatches(employeeIds,100_000);
        int totalUpdatedRecordsBatch =0;
        for (List<Long> batch : batches) {
        	long startBatch = System.currentTimeMillis();
            log.debug("Processing batch of size {}", batch.size());
            totalUpdatedRecordsBatch += employeeRepository.increaseAwardsToEmployees(batch);
            long endBatch = System.currentTimeMillis();
            log.debug("Batch execution completed in {} seconds", String.format("%.2f", (endBatch - startBatch) / 1000.0));
        }
        long batchEnd = System.currentTimeMillis();
        log.info("Batch Processing completed in {} seconds", String.format("%.2f", (batchEnd - batchStart) / 1000.0));
        
        assertThat(totalUpdatedRecordsBatch).isEqualTo(employeeIds.size());
        assertThat(totalUpdatedRecordsHql).isEqualTo(employeeIds.size());
        assertThat(totalUpdatedRecordsNative).isEqualTo(employeeIds.size());
        
        log.info("Performance comparison completed.");

    }
}
