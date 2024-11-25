package com.ninjaone.dundie_awards.service;

import java.util.List;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;

public interface EmployeeService {

	List<EmployeeDto> getAllEmployees();

	EmployeeDto getEmployee(long id);

	EmployeeDto createEmployee(EmployeeDto employeeDto);

	EmployeeDto updateEmployee(long id, EmployeeUpdateRequestDto updateRequest);

	void deleteEmployee(long id);

}