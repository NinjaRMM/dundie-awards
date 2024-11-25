package com.ninjaone.dundie_awards.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;
import com.ninjaone.dundie_awards.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@RestController
public class EmployeeControllerImpl implements EmployeeController {

    private final EmployeeService employeeService;

    @Override
    public List<EmployeeDto> getAllEmployees() {
        log.info("GET /employees - Fetching all employees.");
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        log.info("GET /employees - Successfully fetched {} employees.", employees.size());
        return employees;
    }

    @Override
    public EmployeeDto getEmployeeById(long id) {
        log.info("GET /employees/{} - Fetching employee by ID.", id);
        EmployeeDto employee = employeeService.getEmployee(id);
        log.info("GET /employees/{} - Successfully fetched employee: {}.", id, employee);
        return employee;
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employee) {
        log.info("POST /employees - Creating a new employee with data: {}.", employee);
        EmployeeDto createdEmployee = employeeService.createEmployee(employee);
        log.info("POST /employees - Successfully created employee: {}.", createdEmployee);
        return createdEmployee;
    }

    @Override
    public EmployeeDto updateEmployee(long id, EmployeeUpdateRequestDto updateRequest) {
        log.info("PUT /employees/{} - Updating employee with data: {}.", id, updateRequest);
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, updateRequest);
        log.info("PUT /employees/{} - Successfully updated employee: {}.", id, updatedEmployee);
        return updatedEmployee;
    }

    @Override
    public void deleteEmployee(long id) {
        log.info("DELETE /employees/{} - Deleting employee.", id);
        employeeService.deleteEmployee(id);
        log.info("DELETE /employees/{} - Successfully deleted employee.", id);
    }
}
