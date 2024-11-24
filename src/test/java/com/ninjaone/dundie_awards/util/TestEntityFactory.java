package com.ninjaone.dundie_awards.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestEntityFactory {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

    public Employee createEmployee(String firstName, String lastName, int dundieAwards, Long organizationId) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDundieAwards(dundieAwards);
        employee.setOrganization(Organization.builder().id(organizationId).build());
        return employee;
    }
    
    @SneakyThrows
    public String createEmployeeJson(String firstName, String lastName, int dundieAwards, Long organizationId) {
    	return objectMapper.writeValueAsString(createEmployee(firstName, lastName, dundieAwards, organizationId));
    }
}
