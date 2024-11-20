package com.ninjaone.dundie_awards.mapper;

import com.ninjaone.dundie_awards.dto.EmployeeRequestDTO;
import com.ninjaone.dundie_awards.dto.EmployeeResponseDTO;
import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.model.Employee;

public class EmployeeMapper {
    public static EmployeeResponseDTO toDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDundieAwards(employee.getDundieAwards());
        dto.setOrganization(OrganizationMapper.toDTO(employee.getOrganization()));
        return dto;
    }

    public static Employee toEntity(EmployeeRequestDTO employeeDto, OrganizationDTO organizationDto) {
        Employee employee = new Employee();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setDundieAwards(employeeDto.getDundieAwards());
        employee.setOrganization(OrganizationMapper.toEntity(organizationDto));  // You can fetch the organization in the service layer
        return employee;
    }
}
