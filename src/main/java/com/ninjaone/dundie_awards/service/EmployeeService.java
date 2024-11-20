package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.AwardDundieDTO;
import com.ninjaone.dundie_awards.dto.EmployeeRequestDTO;
import com.ninjaone.dundie_awards.dto.EmployeeResponseDTO;
import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.exception.NotFoundException;
import com.ninjaone.dundie_awards.mapper.EmployeeMapper;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationService organizationService;

    public void deleteEmployee(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if(optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employeeRepository.delete(employee);
        } else {
            throw new NotFoundException("Employee Not Found"); // throw not found exception
        }
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeDetails) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if(optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setFirstName(employeeDetails.getFirstName());
            employee.setLastName(employeeDetails.getLastName());
            Employee updatedEmployee = employeeRepository.save(employee);

            return EmployeeMapper.toDTO(updatedEmployee);
        } else {
            throw new NotFoundException("Employee Not Found");
        }
    }

    public EmployeeResponseDTO findEmployee(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if(optionalEmployee.isPresent()) {
            return EmployeeMapper.toDTO(optionalEmployee.get());

        } else {
            throw new NotFoundException("Employee Not Found");
        }
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeDTO) {
        OrganizationDTO orgDTO = organizationService.findById(employeeDTO.getOrganizationId());
        Employee emp = EmployeeMapper.toEntity(employeeDTO, orgDTO);
        Employee savedEmployee = employeeRepository.save(emp);
        return EmployeeMapper.toDTO(savedEmployee);
    }

    public List<EmployeeResponseDTO> findAll() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeResponseDTO awardDundie(Long id, AwardDundieDTO awardDundieDTO) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if(optionalEmployee.isPresent()) {
            Employee emp = optionalEmployee.get();
            emp.setDundieAwards(emp.getDundieAwards()+awardDundieDTO.getAwardQuantity());
            Employee result = employeeRepository.save(emp);
            return EmployeeMapper.toDTO(result);
        }
        else {
            throw new NotFoundException("Employee Not Found");
        }
    }
}
