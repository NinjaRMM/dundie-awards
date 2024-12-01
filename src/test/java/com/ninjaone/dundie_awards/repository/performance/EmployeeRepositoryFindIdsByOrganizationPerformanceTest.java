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

import lombok.extern.slf4j.Slf4j;

@Disabled("This test is currently ignored. Test just to compare performances")
@DataJpaTest(showSql = false)
@Transactional
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings("deprecation")
class EmployeeRepositoryFindIdsByOrganizationPerformanceTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @BeforeEach
    void setup() {
    	
    	employeeRepository.deleteAll();
        organizationRepository.deleteAll();
        
        long start = System.currentTimeMillis();
        log.info("Setup started for performance comparison...");

        Organization organization = Organization.builder().id(1L).build();
        organizationRepository.save(organization);
        organizationRepository.flush();

        List<Employee> employees = LongStream.rangeClosed(1, 3_000_000)
                .mapToObj(id -> Employee.builder().organization(organization).build())
                .toList();

        employeeRepository.saveAll(employees);
        employeeRepository.flush();

        long end = System.currentTimeMillis();
        log.info("Setup completed for performance comparison in {} seconds", String.format("%.2f", (end - start) / 1000.0));
    }

    @Test
    void compareQueryPerformance() {
        long nativeStart = System.currentTimeMillis();
        log.info("Native Query execution started...");
		List<Long> nativeEmployeeIds = employeeRepository.findEmployeeIdsByOrganizationIdNative(1L);
        long nativeEnd = System.currentTimeMillis();
        log.info("Native Query execution completed in {} seconds", String.format("%.2f", (nativeEnd - nativeStart) / 1000.0));

        long hqlStart = System.currentTimeMillis();
        log.info("HQL Query execution started...");
        List<Long> hqlEmployeeIds = employeeRepository.findEmployeeIdsByOrganizationId(1L);
        long hqlEnd = System.currentTimeMillis();
        log.info("HQL Query execution completed in {} seconds", String.format("%.2f", (hqlEnd - hqlStart) / 1000.0));

        long iterativeStart = System.currentTimeMillis();
        log.info("Iterative Query execution started...");

        int offset = 0;
        int limit = 100_000;
        List<Long> iterativeEmployeeIds;
        int totalRecords = 0;
        do {
            iterativeEmployeeIds = employeeRepository.findEmployeeIdsByOrganizationIdWithPagination(1L, offset, limit);
            totalRecords += iterativeEmployeeIds.size();
            offset += limit;
        } while (!iterativeEmployeeIds.isEmpty());

        long iterativeEnd = System.currentTimeMillis();
        log.info("Iterative Query execution completed in {} seconds", String.format("%.2f", (iterativeEnd - iterativeStart) / 1000.0));

        assertThat(nativeEmployeeIds).hasSize(3_000_000);
        assertThat(hqlEmployeeIds).hasSize(3_000_000);
        assertThat(totalRecords).isEqualTo(3_000_000);
    }
}
