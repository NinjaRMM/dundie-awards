package com.ninjaone.dundie_awards.controller;

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
import com.ninjaone.dundie_awards.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping
	public List<Employee> getAllEmployees() {
		return employeeService.getAllEmployees();
	}
	
	@GetMapping("/{id}")
	public Employee getEmployeeById(@PathVariable("id") long id) {
		return employeeService.getEmployee(id);
	}

	@PostMapping
	@ResponseStatus(CREATED)
	public Employee createEmployee(@RequestBody Employee employee) {
		return employeeService.createEmployee(employee);
	}

	@PutMapping("/{id}")
	public Employee updateEmployee(@PathVariable("id") long id, @RequestBody Employee updateEmployeeRequest) {
		return employeeService.updateEmployee(id, updateEmployeeRequest);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(NO_CONTENT)
	public void deleteEmployee(@PathVariable("id") long id) {
		employeeService.deleteEmployee(id);
	}
}