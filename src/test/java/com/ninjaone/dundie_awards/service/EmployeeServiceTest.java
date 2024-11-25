package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployee;
import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployeeDto;
import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployeeUpdateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateEmployeeTests {

    	@Test
    	void shouldCreateEmployee() {
    	    EmployeeDto employeeDto = createEmployeeDto("Ryan", "Howard", 0, 1L);
    	    Employee employee = createEmployee("Ryan", "Howard", 0, 1L);
    	    given(employeeRepository.save(employee)).willReturn(employee);

    	    EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);

    	    assertThat(createdEmployee).isEqualTo(EmployeeDto.toDto(employee));
    	    verify(employeeRepository).save(employee);
    	}


        @Test
        void shouldCreateEmployeeWithNullOrganization() {
            Employee employee = createEmployee("Ryan", "Howard", 0, null);
            employee.setOrganization(null);
            given(employeeRepository.save(employee)).willReturn(employee);

            Employee createdEmployee = employeeService.createEmployee(EmployeeDto.toDto(employee)).toEntity();

            assertThat(createdEmployee.getOrganization()).isNull();
            verify(employeeRepository).save(employee);
        }
        
    }

    @Nested
    class GetEmployeeTests {

        @Test
        void shouldGetEmployeeById() {
            Employee employee = createEmployee("John", "Doe", 0, 1L);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

            EmployeeDto foundEmployee = employeeService.getEmployee(1L);

            assertThat(foundEmployee).isEqualTo(EmployeeDto.toDto(employee));
            verify(employeeRepository).findById(1L);
        }

        @Test
        void shouldThrowExceptionForNonExistingEmployee() {
            given(employeeRepository.findById(999L)).willReturn(Optional.empty());

            NoSuchElementException exception = catchThrowableOfType(
                () -> employeeService.getEmployee(999L),
                NoSuchElementException.class
            );

            assertThat(exception.getMessage()).isEqualTo("Employee with id: 999 not found");
        }
    }

    @Nested
    class UpdateEmployeeTests {

    	@Test
    	void shouldUpdateEmployee() {
    	    EmployeeUpdateRequestDto updateRequest = createEmployeeUpdateRequestDto("Ryan", "Howard", 5, 1L);
    	    Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1L);
    	    Employee expectedUpdatedEmployee = existingEmployee.toBuilder()
    	            .dundieAwards(5)
    	            .firstName("Ryan")
    	            .lastName("Howard")
    	            .organization(Organization.builder().id(1L).build())
    	            .build();
    	    
    	    given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
    	    given(employeeRepository.save(expectedUpdatedEmployee)).willReturn(expectedUpdatedEmployee);

    	    EmployeeDto updatedEmployee = employeeService.updateEmployee(1L, updateRequest);

    	    assertThat(updatedEmployee).isEqualTo(EmployeeDto.toDto(expectedUpdatedEmployee));
    	    verify(employeeRepository).findById(1L);
    	    verify(employeeRepository).save(expectedUpdatedEmployee);
    	}


    	@Test
    	void shouldUpdateEmployeeWithNullOrganization() {
    	    Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1L);
    	    EmployeeUpdateRequestDto updateRequest = createEmployeeUpdateRequestDto("Ryan", "Howard", 5, null);

    	    Employee expectedUpdatedEmployee = existingEmployee.toBuilder()
    	            .organization(null)
    	            .dundieAwards(5)
    	            .firstName("Ryan")
    	            .lastName("Howard")
    	            .build();

    	    given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
    	    given(employeeRepository.save(expectedUpdatedEmployee)).willReturn(expectedUpdatedEmployee);

    	    EmployeeDto updatedEmployee = employeeService.updateEmployee(1L, updateRequest);

    	    assertThat(updatedEmployee.organizationId()).isNull();
    	    assertThat(updatedEmployee.dundieAwards()).isEqualTo(5);
    	    assertThat(updatedEmployee.firstName()).isEqualTo("Ryan");
    	    assertThat(updatedEmployee.lastName()).isEqualTo("Howard");

    	    verify(employeeRepository).findById(1L);
    	    verify(employeeRepository).save(expectedUpdatedEmployee);
    	}

    }

    @Nested
    class DeleteEmployeeTests {

        @Test
        void shouldDeleteEmployee() {
            Employee employee = createEmployee("Ryan", "Howard", 0, 1L);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

            employeeService.deleteEmployee(1L);

            verify(employeeRepository).findById(1L);
            verify(employeeRepository).delete(employee);
        }

        @Test
        void shouldThrowExceptionForNonExistingEmployee() {
            given(employeeRepository.findById(999L)).willReturn(Optional.empty());

            NoSuchElementException exception = catchThrowableOfType(
                () -> employeeService.deleteEmployee(999L),
                NoSuchElementException.class
            );

            assertThat(exception.getMessage()).isEqualTo("Employee with id: 999 not found");
        }
    }
}
