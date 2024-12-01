package com.ninjaone.dundie_awards.service.impl;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.service.EmployeeService;
import com.ninjaone.dundie_awards.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrganizationService organizationService;
    private final AppProperties appProperties;
    
    @Override
	public List<EmployeeDto> getAllEmployees() {
        log.debug("getAllEmployees - Fetching all employees.");
        List<EmployeeDto> employees = null;
        if(appProperties.enableTestBehavior()) {
        	log.debug("getAllEmployees - Fetching all employees. Except the high-loaded ones");
        	employees = employeeRepository.findAllByOrganizationIdsNotIn(
        			List.of(
        			Organization.FRAJOLA.getId(),
        			Organization.TOM.getId()))
                .stream()
                .map(EmployeeDto::toDto)
                .collect(Collectors.toList());
        }else {
        	employees = employeeRepository.findAll()
                    .stream()
                    .map(EmployeeDto::toDto)
                    .collect(Collectors.toList());
        }
        log.debug("getAllEmployees - Fetched {} employees.  Except from Garfield organization.", employees.size());
        return employees;
    }

    @Override
	public EmployeeDto getEmployee(long id) {
        log.info("getEmployee - Fetching employee with ID: {}", id);
        Employee employee = findEmployeeById(id);
        EmployeeDto employeeDto = EmployeeDto.toDto(employee);
        log.info("getEmployee - Fetched employee with ID: {}: {}", id, employeeDto);
        return employeeDto;
    }
    
    @Override
    public List<Long> getEmployeesIdsByOrganization(UUID uuid, long organizationId) {
        log.info("UUID: {} - getEmployeesIdsByOrganization - Fetching employee IDs for Organization ID: {}", uuid, organizationId);
        List<Long> employeeIds = employeeRepository.findEmployeeIdsByOrganizationId(organizationId);
        log.info("UUID: {} - getEmployeesIdsByOrganization - Found {} employee IDs for Organization ID: {}", uuid, employeeIds.size(), organizationId);
        return employeeIds;
    }

    @Override
	public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        log.info("createEmployee - Creating employee: {}", employeeDto);
        EmployeeDto savedEmployeeDto = EmployeeDto.toDto(employeeRepository.save(employeeDto.toEntity()));
        log.info("createEmployee - Created employee: {}", savedEmployeeDto);
        return savedEmployeeDto;
    }

    @Override
	public EmployeeDto updateEmployee(long id, EmployeeUpdateRequestDto updateRequest) {
        log.info("updateEmployee - Updating employee with ID: {} using update request: {}", id, updateRequest);
        Employee existingEmployee = findEmployeeById(id);
        Employee updatedEmployee = updateRequest.toEntity(existingEmployee, organizationService);
        EmployeeDto updatedEmployeeDto = EmployeeDto.toDto(employeeRepository.save(updatedEmployee));
        log.info("updateEmployee - Updated employee with ID: {}: {}", id, updatedEmployeeDto);
        return updatedEmployeeDto;
    }
    
    @Override
    @Transactional
    public void updateDundieAwards(UUID uuid, Long employeeId, int dundieAwards) {
        log.info("UUID: {} - Updating Dundie Awards for Employee ID: {} to {}", uuid, employeeId, dundieAwards);
        int affectedRows = employeeRepository.updateDundieAwards(employeeId, dundieAwards);
        if (affectedRows > 0) {
            log.info("UUID: {} - Successfully updated Dundie Awards for Employee ID: {}", uuid, employeeId);
        } else {
            log.warn("UUID: {} - No Employee found with ID: {}. No updates applied.", uuid, employeeId);
        }
    }


    @Override
    public int addDundieAwardToEmployees(UUID uuid, Long organizationId) {
    	log.info("UUID: {} - addDundieAwardToEmployees - Adding awards to organization with ID: {}", uuid, organizationId);
    	int updatedRecords = employeeRepository.increaseAwardsToEmployeesNative(organizationId);
    	log.info("UUID: {} - addDundieAwardToEmployees - Successfully updated {} employees", uuid, updatedRecords);
    	return updatedRecords;
    }
    
    @Override
    public int removeDundieAwardToEmployees(UUID uuid, Long organizationId) {
        log.info("UUID: {} - Removing Dundie Awards for Organization ID: {}", uuid, organizationId);
        int affectedRows = employeeRepository.decreaseAwardsToEmployeesNative(organizationId);
        log.info("UUID: {} - Removed Dundie Awards for {} employees in Organization ID: {}", uuid, affectedRows, organizationId);
        return affectedRows;
    }
    
    @Override
	public String fetchEmployeeRollbackData(UUID uuid, Long organizationId) {
    	log.info("UUID: {} - fetchEmployeeRollbackData - Fetching award changes to organization with ID: {}", uuid, organizationId);
    	String rollbackData = employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(organizationId);
    	log.info("UUID: {} - fetchEmployeeRollbackData - Successfully fetched", uuid);
    	return rollbackData;
	}

    @Override
    public String fetchEmployeeComparisonData(UUID uuid, Long organizationId) {
    	log.info("UUID: {} - fetchEmployeeComparisonData - Fetching award changes to organization with ID: {}", uuid, organizationId);
    	String rollbackData = employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(organizationId);
    	log.info("UUID: {} - fetchEmployeeComparisonData - Successfully fetched", uuid);
    	return rollbackData;
    }
    
    @Override
	public void deleteEmployee(long id) {
        log.info("deleteEmployee - Deleting employee with ID: {}", id);
        employeeRepository.delete(findEmployeeById(id));
        log.info("deleteEmployee - Deleted employee with ID: {}", id);
    }
    
    /*
     * bad performance
     */
    @Override
    @Transactional
    @Deprecated
    public void rollbackDundieAwards(String tableName, Set<SimpleEntry<Long, Integer>> parsedData) {
    	employeeRepository.createTemporaryTable(tableName);
        try {
            parsedData.forEach(entry ->
                employeeRepository.insertIntoTemporaryTable(tableName, entry.getKey(), entry.getValue())
            );
            employeeRepository.updateEmployeesFromTemporaryTable(tableName);
        } finally {
            employeeRepository.dropTemporaryTable(tableName);
        }
    }

    private Employee findEmployeeById(long id) {
        log.info("findEmployeeById - Finding employee with ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("findEmployeeById - Employee with ID: {} not found.", id);
                    return employeeNotFoundException.apply(id);
                });
    }

}
