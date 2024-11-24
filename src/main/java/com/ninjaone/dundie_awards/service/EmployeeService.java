package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;
import static java.util.Optional.ofNullable;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

	
	private final EmployeeRepository employeeRepository;
    private final OrganizationService organizationService;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(long id) {
    	return findEmployeeById(id);
    }

    public Employee createEmployee(Employee employee) {
    	 ofNullable(employee.getOrganization())
         	.ifPresent(organization -> organizationService.ensureValidOrganization(organization.getId()));
		return employeeRepository.save(employee);
    }

    
    public Employee updateEmployee(long id, Employee updateRequest) {
    	Employee existingEmployee = findEmployeeById(id);

    	Employee updatedEmployee = existingEmployee.toBuilder()
    	        .organization(
	        		ofNullable(updateRequest.getOrganization())
	        			.map(org -> organizationService.getValidOrganization(org.getId()))
	        			.orElse(null))
    	        .firstName(updateRequest.getFirstName())
    	        .lastName(updateRequest.getLastName())
    	        .dundieAwards(updateRequest.getDundieAwards())
    	        .build();
    	
		return employeeRepository.save(updatedEmployee);
    }

    public void deleteEmployee(long id) {
		employeeRepository.delete(findEmployeeById(id));
    }
    
    private Employee findEmployeeById(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> employeeNotFoundException.apply(id));
    }
}
