package com.ninjaone.dundie_awards.controller;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;
import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.organizationNotValidException;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;

    public EmployeeController(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository) {
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

	@GetMapping
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Employee getEmployeeById(@PathVariable("id") long id) {
		return employeeRepository.findById(id).orElseThrow(() -> employeeNotFoundException.apply(id));
	}

	@PostMapping
	@ResponseStatus(CREATED)
	public Employee createEmployee(@RequestBody Employee employee) {
		//validate organization
		//TODO: move validation to DTO/entity map layer
		ofNullable(employee.getOrganization())
		        .map(org -> org.getId())
		        .filter(organizationRepository::existsById)
		        .orElseThrow(() -> organizationNotValidException.apply(employee.getOrganization().getId()));
		return employeeRepository.save(employee);
	}

	@PutMapping("/{id}")
	public Employee updateEmployee(@PathVariable("id") long id, @RequestBody Employee updateEmployeeRequest) {
		Employee employee = employeeRepository.findById(id).orElseThrow(() -> employeeNotFoundException.apply(id));
		//validation and retrieve organization
		//TODO: move validation to DTO/entity map layer
		Organization organization = ofNullable(updateEmployeeRequest.getOrganization())
		        .map(org -> org.getId())
		        .flatMap(organizationRepository::findById)
		        .orElseThrow(() -> organizationNotValidException.apply(updateEmployeeRequest.getOrganization().getId()));
		employee.setFirstName(updateEmployeeRequest.getFirstName());
		employee.setLastName(updateEmployeeRequest.getLastName());
		employee.setDundieAwards(updateEmployeeRequest.getDundieAwards());
		employee.setOrganization(organization);
		return employeeRepository.save(employee);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(NO_CONTENT)
	public void deleteEmployee(@PathVariable("id") long id) {
		Employee employee = employeeRepository.findById(id).orElseThrow(() -> employeeNotFoundException.apply(id));
		employeeRepository.delete(employee);
	}
}