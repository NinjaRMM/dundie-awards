package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.error.NotFoundException;
import com.ninjaone.dundie_awards.organization.Organization;
import com.ninjaone.dundie_awards.organization.OrganizationRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ninjaone.dundie_awards.infrastructure.DundieResource.EMPLOYEE;
import static com.ninjaone.dundie_awards.infrastructure.DundieResource.ORGANIZATION;
import static com.ninjaone.dundie_awards.util.SanitizationUtils.mutateIfNotNull;
import static com.ninjaone.dundie_awards.util.SanitizationUtils.mutateTrimmedStringIfNotNull;
import static java.util.Optional.ofNullable;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @NotNull
    public List<EmployeeRecord> getAllEmployees() {
        return employeeRepository.findAll()
                .stream().map(EmployeeRecord::fromDb)
                .toList();
    }

    EmployeeRecord getEmployee(long id) {
        return employeeRepository.findById(id)
                .map(EmployeeRecord::fromDb)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE));
    }

    @NotNull
    EmployeeRecord createEmployee(EmployeeRecord employee) {
        return EmployeeRecord.fromDb(
                employeeRepository.save(employee.toDb())
        );
    }

    @Transactional(isolation = REPEATABLE_READ)
    @NotNull
    EmployeeRecord updateEmployee(long id, @NotNull UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE));

        // Could be better to sanitise in the controller
        mutateTrimmedStringIfNotNull(request::firstName, employee::setFirstName);
        mutateTrimmedStringIfNotNull(request::lastName, employee::setLastName);
        mutateIfNotNull(request::dundieAwards, employee::setDundieAwards);

        ofNullable(request.organizationId())
                .ifPresent(it -> {
                    Organization newOrganization = organizationRepository.findById(it)
                            .orElseThrow(() -> new NotFoundException(it, ORGANIZATION));
                    employee.setOrganization(newOrganization);
                });

        return EmployeeRecord.fromDb(employee);
    }

    void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }
}
