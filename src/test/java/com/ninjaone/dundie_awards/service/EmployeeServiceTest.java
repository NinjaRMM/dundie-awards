package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.dto.*;
import com.ninjaone.dundie_awards.events.MessageBroker;
import com.ninjaone.dundie_awards.exception.NotFoundException;
import com.ninjaone.dundie_awards.mapper.EmployeeMapper;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private AwardsCache awardsCache;

    @Mock
    private MessageBroker messageBroker;

    @Test
    void testDeleteEmployee_Success() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.deleteEmployee(employeeId));
    }

    @Test
    void testUpdateEmployee_Success() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDundieAwards(0);
        employee.setOrganization(new Organization("test"));

        EmployeeRequestDTO updateDTO = new EmployeeRequestDTO();
        updateDTO.setFirstName("UpdatedFirstName");
        updateDTO.setLastName("UpdatedLastName");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO responseDTO = employeeService.updateEmployee(employeeId, updateDTO);

        assertEquals("UpdatedFirstName", responseDTO.getFirstName());
        assertEquals("UpdatedLastName", responseDTO.getLastName());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        Long employeeId = 1L;
        EmployeeRequestDTO updateDTO = new EmployeeRequestDTO();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.updateEmployee(employeeId, updateDTO));
    }

    @Test
    void testFindEmployee_Success() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDundieAwards(0);
        employee.setOrganization(new Organization("test"));

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO responseDTO = employeeService.findEmployee(employeeId);

        assertNotNull(responseDTO);
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void testFindEmployee_NotFound() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.findEmployee(employeeId));
    }

    @Test
    void testCreateEmployee_Success() {
        EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setOrganizationId(1L);
        employeeRequestDTO.setDundieAwards(5);
        employeeRequestDTO.setFirstName("TST");
        employeeRequestDTO.setLastName("TST2");

        Employee employee = new Employee();
        employee.setId(0);
        employee.setOrganization(new Organization("orgTest"));
        employee.setLastName("TST2");
        employee.setFirstName("TST");
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(1L);
        organizationDTO.setName("orgTest");

        when(organizationService.findById(1L)).thenReturn(organizationDTO);
        //when(EmployeeMapper.toEntity(employeeRequestDTO, organizationDTO)).thenReturn(employee);
        when(employeeRepository.save(any())).thenReturn(employee);
        //when(EmployeeMapper.toDTO(employee)).thenReturn(new EmployeeResponseDTO());

        EmployeeResponseDTO responseDTO = employeeService.createEmployee(employeeRequestDTO);

        assertNotNull(responseDTO);
        verify(awardsCache).addAwards(5);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testAwardDundie_Success() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setDundieAwards(3);
        employee.setOrganization(new Organization("testOrg"));

        AwardDundieDTO awardDundieDTO = new AwardDundieDTO();
        awardDundieDTO.setAwardQuantity(2);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO responseDTO = employeeService.awardDundie(employeeId, awardDundieDTO);

        assertNotNull(responseDTO);
        verify(awardsCache).addAwards(2);
        verify(messageBroker).sendMessage(any(ActivityEventDTO.class));
        verify(employeeRepository).save(employee);
    }

    @Test
    void testAwardDundie_NotFound() {
        Long employeeId = 1L;
        AwardDundieDTO awardDundieDTO = new AwardDundieDTO();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.awardDundie(employeeId, awardDundieDTO));
    }

}