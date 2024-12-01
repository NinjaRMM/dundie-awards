package com.ninjaone.dundie_awards.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest(showSql = false)
@Transactional
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeRepositoryFindByOrganizationIdTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @BeforeEach
    void setup() {
    	employeeRepository.deleteAll();
        organizationRepository.deleteAll();
        
        log.info("Setup started for findByOrganizationId tests...");

        Organization organization = Organization.builder().id(1L).build();
        organizationRepository.save(organization);
        organizationRepository.flush();

        List<Employee> employees = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            employees.add(Employee.builder().organization(organization).build());
        }

        employeeRepository.saveAll(employees);
        organizationRepository.flush();
        log.info("Setup completed for findByOrganizationId tests.");
    }

    @Test
    void shouldFetchEmployeeIdsByOrganizationId() {
        List<Long> hqlEmployeeIds = employeeRepository.findEmployeeIdsByOrganizationId(1L);
        log.info("HQL query fetched {} records", hqlEmployeeIds.size());
        assertThat(hqlEmployeeIds).isNotNull();
    }

}
