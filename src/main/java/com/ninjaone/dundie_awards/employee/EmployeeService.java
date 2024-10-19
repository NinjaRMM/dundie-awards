package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.error.NotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ninjaone.dundie_awards.infrastructure.DundieResource.EMPLOYEE;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @NotNull
    List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @NotNull
    Employee getEmployee(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE));
    }

    @NotNull
    Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional(isolation = REPEATABLE_READ)
    @NotNull
    Employee updateEmployee(long id, @NotNull Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());

        return employee;
    }

    @NotNull
    ResponseEntity<Map<String, Boolean>> deleteEmployee(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (!optionalEmployee.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Employee employee = optionalEmployee.get();
        employeeRepository.delete(employee);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
