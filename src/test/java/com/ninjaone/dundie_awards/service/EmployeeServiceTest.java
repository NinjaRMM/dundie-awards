package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.TestEntityFactory.createEmployee;
import static com.ninjaone.dundie_awards.TestEntityFactory.createEmployeeDto;
import static com.ninjaone.dundie_awards.TestEntityFactory.createEmployeeUpdateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import com.ninjaone.dundie_awards.service.impl.EmployeeServiceImpl;
import com.ninjaone.dundie_awards.service.impl.OrganizationServiceImpl;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OrganizationServiceImpl organizationService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

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
    class GetEmployeeIdsByOrganizationTests {

        @Test
        void shouldReturnEmployeeIdsForOrganization() {
            long organizationId = 1L;
            List<Long> employeeIds = List.of(1L, 2L, 3L);
            given(employeeRepository.findEmployeeIdsByOrganizationId(organizationId)).willReturn(employeeIds);

            List<Long> result = employeeService.getEmployeesIdsByOrganization(UUID.randomUUID(), organizationId);

            assertThat(result).isEqualTo(employeeIds);
            verify(employeeRepository).findEmployeeIdsByOrganizationId(organizationId);
        }

        @Test
        void shouldReturnEmptyListForOrganizationWithoutEmployees() {
            long organizationId = 999L;
            given(employeeRepository.findEmployeeIdsByOrganizationId(organizationId)).willReturn(List.of());

            List<Long> result = employeeService.getEmployeesIdsByOrganization(UUID.randomUUID(),organizationId);

            assertThat(result).isEmpty();
            verify(employeeRepository).findEmployeeIdsByOrganizationId(organizationId);
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
    	            .organization(Organization.builder()
    	            		.id(1L)
    	            		.blocked(false)
    	            		.build())
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
    	
    	@Test
    	void shouldUpdateDundieAwardsSuccessfully() {
    	    UUID uuid = UUID.randomUUID();
    	    Long employeeId = 1L;
    	    int dundieAwards = 5;
    	    given(employeeRepository.updateDundieAwards(employeeId, dundieAwards)).willReturn(1);
    	    employeeService.updateDundieAwards(uuid, employeeId, dundieAwards);
    	    verify(employeeRepository).updateDundieAwards(employeeId, dundieAwards);
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
    
    @Nested
    class FetchEmployeeRollbackDataTests {

        @Test
        void shouldFetchEmployeeRollbackData() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 1L;
            String rollbackData = "1,8|2,9|3,2";
            given(employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(organizationId)).willReturn(rollbackData);

            String result = employeeService.fetchEmployeeRollbackData(uuid, organizationId);

            assertThat(result).isEqualTo(rollbackData);
            verify(employeeRepository).findConcatenatedEmployeeDataByOrganizationIdNative(organizationId);
        }

        @Test
        void shouldReturnEmptyStringIfNoRollbackDataFound() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 999L;
            given(employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(organizationId)).willReturn("");

            String result = employeeService.fetchEmployeeRollbackData(uuid, organizationId);

            assertThat(result).isEmpty();
            verify(employeeRepository).findConcatenatedEmployeeDataByOrganizationIdNative(organizationId);
        }
    }
    
    @Nested
    class DundieAwardModificationTests {

        @Test
        void shouldAddDundieAwardToEmployees() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 1L;
            int updatedRecords = 5;
            given(employeeRepository.increaseAwardsToEmployeesNative(organizationId)).willReturn(updatedRecords);

            int result = employeeService.addDundieAwardToEmployees(uuid, organizationId);

            assertThat(result).isEqualTo(updatedRecords);
            verify(employeeRepository).increaseAwardsToEmployeesNative(organizationId);
        }

        @Test
        void shouldRemoveDundieAwardToEmployees() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 1L;
            int affectedRows = 3;
            given(employeeRepository.decreaseAwardsToEmployeesNative(organizationId)).willReturn(affectedRows);

            int result = employeeService.removeDundieAwardToEmployees(uuid, organizationId);

            assertThat(result).isEqualTo(affectedRows);
            verify(employeeRepository).decreaseAwardsToEmployeesNative(organizationId);
        }
    }
    
    @Nested
    class RollbackDundieAwardsTests {

        @Test
        void shouldRollbackDundieAwards() {
            String tableName = "temp_123";
            Set<SimpleEntry<Long, Integer>> parsedData = Set.of(
                    new SimpleEntry<>(1L, 5),
                    new SimpleEntry<>(2L, 10)
            );

            // Mock behavior for each operation
            employeeService.rollbackDundieAwards(tableName, parsedData);

            // Verify each method call
            verify(employeeRepository).createTemporaryTable(tableName);
            parsedData.forEach(entry -> 
                verify(employeeRepository).insertIntoTemporaryTable(tableName, entry.getKey(), entry.getValue())
            );
            verify(employeeRepository).updateEmployeesFromTemporaryTable(tableName);
            verify(employeeRepository).dropTemporaryTable(tableName);
        }
        
        @Test
        void shouldThrowExceptionWhenCreatingTemporaryTableFails() {
            String tableName = "temp_123";
            Set<SimpleEntry<Long, Integer>> parsedData = Set.of(
                    new SimpleEntry<>(1L, 5),
                    new SimpleEntry<>(2L, 10)
            );

            doThrow(new RuntimeException("Error creating table")).when(employeeRepository).createTemporaryTable(tableName);

            assertThatThrownBy(() -> employeeService.rollbackDundieAwards(tableName, parsedData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error creating table");

            verify(employeeRepository).createTemporaryTable(tableName);
            verify(employeeRepository, never()).insertIntoTemporaryTable(any(), any(), any());
            verify(employeeRepository, never()).updateEmployeesFromTemporaryTable(any());
            verify(employeeRepository, never()).dropTemporaryTable(any());
        }
    }
    
    @Nested
    class FetchEmployeeComparisonDataTests {

        @Test
        void shouldFetchEmployeeComparisonData() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 1L;
            String comparisonData = "1,10|2,15|3,20";

            given(employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(organizationId))
                    .willReturn(comparisonData);

            String result = employeeService.fetchEmployeeComparisonData(uuid, organizationId);

            assertThat(result).isEqualTo(comparisonData);
            verify(employeeRepository, org.mockito.Mockito.times(1))
                    .findConcatenatedEmployeeDataByOrganizationIdNative(organizationId);
        }

        @Test
        void shouldReturnEmptyStringIfNoComparisonDataFound() {
            UUID uuid = UUID.randomUUID();
            Long organizationId = 999L;

            given(employeeRepository.findConcatenatedEmployeeDataByOrganizationIdNative(organizationId))
                    .willReturn("");

            String result = employeeService.fetchEmployeeComparisonData(uuid, organizationId);

            assertThat(result).isEmpty();
            verify(employeeRepository, org.mockito.Mockito.times(1))
                    .findConcatenatedEmployeeDataByOrganizationIdNative(organizationId);
        }
    }




}
