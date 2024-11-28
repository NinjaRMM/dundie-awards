package com.ninjaone.dundie_awards.service;

import java.util.List;
import java.util.UUID;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;

public interface EmployeeService {

	List<EmployeeDto> getAllEmployees();

	EmployeeDto getEmployee(long id);
	
	List<Long> getEmployeesIdsByOrganization(UUID uuid, long organizationId);

	EmployeeDto createEmployee(EmployeeDto employeeDto);

	EmployeeDto updateEmployee(long id, EmployeeUpdateRequestDto updateRequest);

	int addDundieAwardToEmployees(UUID uuid, Long organizationId);
	
	String fetchEmployeeRollbackData(UUID uuid, Long organizationId);
	
	void deleteEmployee(long id);


}