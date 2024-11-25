package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

	
	private final EmployeeRepository employeeRepository;
    private final OrganizationService organizationService;

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeDto::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto getEmployee(long id) {
        Employee employee = findEmployeeById(id);
        return EmployeeDto.toDto(employee);
    }

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
    	return EmployeeDto.toDto(employeeRepository.save(employeeDto.toEntity()));
    }

    public EmployeeDto updateEmployee(long id, EmployeeUpdateRequestDto updateRequest) {
    	Employee existingEmployee = findEmployeeById(id);
    	Employee updatedEmployee = updateRequest.toEntity(existingEmployee, organizationService);
        return EmployeeDto.toDto(employeeRepository.save(updatedEmployee));
    }

    public void deleteEmployee(long id) {
		employeeRepository.delete(findEmployeeById(id));
    }
    
    private Employee findEmployeeById(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> employeeNotFoundException.apply(id));
    }
}
