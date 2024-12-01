package com.ninjaone.dundie_awards.repository.performance;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.AwardOperationLog;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.AwardOperationLogRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.util.RollbackDataComparator;

import lombok.extern.slf4j.Slf4j;

/*
 * 
 * Rollback using temporary table with join update took 66,31 seconds to 10_000 (too bad)
 * 
 * In comparison with simpy deducing times:
 * Save employees started...
 * Save employees finishes in 37,30 seconds
 * Concatenated search to rollback data started ...
 * Concatenated search to rollback data execution completed in 1,93 seconds
 * Saving rollback data
 * Rollback data saved in 0,50 seconds
 * Increase awards started...
 * Increase awards completed in 12,08 seconds
 * Retrieving rollbackdata
 * Retrieving rollbackdata completed in 0,00 seconds
 * Rollback data parsed in 0,79 seconds
 * Decrease awards started...
 * Decrease awards completed in 12,75 seconds
 * Concatenated search to actual data started ...
 * Concatenated search to actual data execution completed in 2,85 seconds
 * All employee rollback data matches the expected parsed data.
 * Rollback verification completed.
 * 
 */
@Disabled("Performance tests are currently ignored")
@DataJpaTest(showSql = false)
@Transactional
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeRepositoryRollbackPerformanceTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AwardOperationLogRepository awardOperationLogRepository;
    
    private List<Long> employeeIds;
    private Organization organization;
    
    private String rollbackData;
    private UUID uuid;
    private Set<AbstractMap.SimpleEntry<Long, Integer>> parsedData;
    private int totalemployees = 3_000_000;
    private List<Employee> employees;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
        organizationRepository.deleteAll();
        awardOperationLogRepository.deleteAll();

        //saving employees
        long start = System.currentTimeMillis();
        log.info("Save employees started...");
        organization = Organization.builder().id(1L).name("Test Organization").build();
        organizationRepository.save(organization);
        organizationRepository.flush();
        employeeIds = LongStream.rangeClosed(1, totalemployees).boxed().toList();
        employees = employeeIds.stream()
                .map(id -> Employee.builder()
                        .id(id)
                        .dundieAwards(ThreadLocalRandom.current().nextInt(0, 13))
                        .organization(organization)
                        .build())
                .toList();
        employeeRepository.saveAll(employees);
        employeeRepository.flush();
        long end = System.currentTimeMillis();
        log.info("Save employees finishes in {} seconds", String.format("%.2f", (end - start) / 1000.0));
        
        //retrieve rollback data
        long nativeStart = System.currentTimeMillis();
        log.info("Concatenated search to rollback data started ...");
        rollbackData = employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(1L);
        long nativeEnd = System.currentTimeMillis();
        log.info("Concatenated search to rollback data execution completed in {} seconds", String.format("%.2f", (nativeEnd - nativeStart) / 1000.0));
        
        //save rollback data
        log.info("Saving rollback data");
        uuid = UUID.randomUUID();
        long startSaving = System.currentTimeMillis();
        awardOperationLogRepository.save(AwardOperationLog.builder()
                .uuid(uuid.toString())
                .rollbackData(rollbackData)
                .build());
        awardOperationLogRepository.flush();
        long endSaving = System.currentTimeMillis();
        log.info("Rollback data saved in {} seconds", String.format("%.2f", (endSaving - startSaving) / 1000.0));
        
        //increase awards
        long incNativeStart = System.currentTimeMillis();
        log.info("Increase awards started...");
        employeeRepository.increaseAwardsToEmployeesNative(organization.getId());
        long incNativeEnd = System.currentTimeMillis();
        log.info("Increase awards completed in {} seconds", String.format("%.2f", (incNativeEnd - incNativeStart) / 1000.0));
        
        
        //retrieve rollback data
        log.info("Retrieving rollbackdata");
        long startRetrieving = System.currentTimeMillis();
        AwardOperationLog logRecord = awardOperationLogRepository.getReferenceById(uuid.toString());
        String savedRollbackData = logRecord.getRollbackData();
        long endRetrieving = System.currentTimeMillis();
        log.info("Retrieving rollbackdata completed in {} seconds", String.format("%.2f", (endRetrieving - startRetrieving) / 1000.0));
        
        
        //parse rollbackdata
        long startProcessing = System.currentTimeMillis();
        parsedData = Arrays.stream(savedRollbackData.split("\\|"))
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
        log.info("Rollback data parsed in {} seconds", String.format("%.2f", (endProcessing - startProcessing) / 1000.0));
        
        
        
    }

    @Test
    void testRollbackDundieAwardsPerformance() {

        // Execute rollback with temporary table and joins
//    	log.info("Rollback started");
//        long startRollback = System.currentTimeMillis();
//        employeeRepository.createTemporaryTable(uuid.toString());
//        try {
//            parsedData.forEach(entry ->
//                employeeRepository.insertIntoTemporaryTable(uuid.toString(), entry.getKey(), entry.getValue())
//            );
//            employeeRepository.updateEmployeesFromTemporaryTable(uuid.toString());
//        } finally {
//            employeeRepository.dropTemporaryTable(uuid.toString());
//        }
//        long endRollback = System.currentTimeMillis();
//        log.info("Rollback completed in {} seconds", String.format("%.2f", (endRollback - startRollback) / 1000.0));

    	//Execute rollback with temporary table and joins
    	long decNativeStart = System.currentTimeMillis();
        log.info("Decrease awards started...");
        employeeRepository.decreaseAwardsToEmployeesNative(organization.getId());
        long decNativeEnd = System.currentTimeMillis();
        log.info("Decrease awards completed in {} seconds", String.format("%.2f", (decNativeEnd - decNativeStart) / 1000.0));
        
        //forcing diff
        employeeRepository.updateDundieAwards(666L, 999);

        //retrieve actual data in same format of rollback data to comparison
        long nativeStart = System.currentTimeMillis();
        log.info("Concatenated search to actual data started ...");
        String afterDecreaseData = employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(1L);
        long nativeEnd = System.currentTimeMillis();
        log.info("Concatenated search to actual data execution completed in {} seconds", String.format("%.2f", (nativeEnd - nativeStart) / 1000.0));

        log.info("Comparing differences...");
        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(rollbackData, afterDecreaseData);
        log.info("Comparing differences execution completed in {} seconds", String.format("%.2f", (nativeEnd - nativeStart) / 1000.0));
        differences.forEach(diff -> {
            log.warn("UUID: {} - Unexpected difference detected for Organization ID: {}. Employee ID: {}, Original Dundie Awards: {}, Unexpected Dundie Awards: {}, Adjusting to: {}",
                uuid, organization.getId(), diff.id(), diff.originalValue(), diff.unexpectedValue(), diff.originalValue());
            employeeRepository.updateDundieAwards(diff.id(), diff.originalValue());
        });

        //retrieve actual data in same format of rollback data to comparison
        long nativeStart1 = System.currentTimeMillis();
        log.info("Concatenated search to actual data after diff started ...");
        String afterDecreaseDataAgain = employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(1L);
        long nativeEnd1 = System.currentTimeMillis();
        log.info("Concatenated search to actual data after diff execution completed in {} seconds", String.format("%.2f", (nativeEnd1 - nativeStart1) / 1000.0));

        
        assert(afterDecreaseDataAgain.equals(rollbackData));
        assert(differences.size()==1);
        
        log.info("All employee rollback data matches the expected parsed data.");
        log.info("Rollback verification completed.");
    }
}
