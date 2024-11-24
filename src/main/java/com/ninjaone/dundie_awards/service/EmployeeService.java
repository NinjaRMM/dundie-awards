package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;
import static java.util.Optional.ofNullable;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;

@Service
public class EmployeeService {

	
	private final EmployeeRepository employeeRepository;
    private final OrganizationService organizationService;

    public EmployeeService(EmployeeRepository employeeRepository, OrganizationService organizationService) {
        this.employeeRepository = employeeRepository;
        this.organizationService = organizationService;
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(long id) {
    	return employeeRepository.findById(id).orElseThrow(() -> employeeNotFoundException.apply(id));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
    	 ofNullable(employee.getOrganization())
         	.ifPresent(organization -> organizationService.ensureValidOrganization(organization.getId()));
		return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(long id, Employee updateEmployeeRequest) {
    	Employee employee = employeeRepository.findById(id).orElseThrow(() -> employeeNotFoundException.apply(id));
    	ofNullable(updateEmployeeRequest.getOrganization()).ifPresentOrElse(
            organization -> employee.setOrganization(organizationService.getValidOrganization(organization.getId())),
            () -> employee.setOrganization(null)
        );
		employee.setFirstName(updateEmployeeRequest.getFirstName());
		employee.setLastName(updateEmployeeRequest.getLastName());
		employee.setDundieAwards(updateEmployeeRequest.getDundieAwards());
		return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(long id) {
    	Employee employee = employeeRepository.findById(id).orElseThrow(() -> employeeNotFoundException.apply(id));
		employeeRepository.delete(employee);
    }
    

}
