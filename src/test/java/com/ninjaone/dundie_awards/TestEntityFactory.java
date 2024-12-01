package com.ninjaone.dundie_awards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;
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
        employee.setOrganization(Organization.builder().id(organizationId).blocked(false).build());
        return employee;
    }
    
    @SneakyThrows
    public String createEmployeeJson(String firstName, String lastName, int dundieAwards, Long organizationId) {
    	return objectMapper.writeValueAsString(createEmployeeDto(firstName, lastName, dundieAwards, organizationId));
    }
    
    public EmployeeDto createEmployeeDto(String firstName, String lastName, int dundieAwards, Long organizationId) {
        return EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dundieAwards(dundieAwards)
                .organizationId(organizationId)
                .organizationName(organizationId != null ? "Organization Name" : null)
                .build();
    }

    public EmployeeUpdateRequestDto createEmployeeUpdateRequestDto(
            String firstName, String lastName, int dundieAwards, Long organizationId) {
        return EmployeeUpdateRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dundieAwards(dundieAwards)
                .organizationId(organizationId)
                .build();
    }

}
