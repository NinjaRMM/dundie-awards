package com.ninjaone.dundie_awards.controller;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;
import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployeeDto;
import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployeeUpdateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.controller.rest.EmployeeController;
import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;
import com.ninjaone.dundie_awards.service.EmployeeService;

class EmployeeControllerUnitTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateEmployeeTests {

        @Test
        void shouldCreateEmployee() {
            EmployeeDto employeeDto = createEmployeeDto("Ryan", "Howard", 0, 1L);
            given(employeeService.createEmployee(employeeDto)).willReturn(employeeDto);

            EmployeeDto createdEmployee = employeeController.createEmployee(employeeDto);

            assertThat(createdEmployee).isEqualTo(employeeDto);
            verify(employeeService).createEmployee(employeeDto);
        }

        @Test
        void shouldCreateEmployeeWithNullOrganization() {
            EmployeeDto employeeDto = createEmployeeDto("Ryan", "Howard", 0, null);
            given(employeeService.createEmployee(employeeDto)).willReturn(employeeDto);

            EmployeeDto createdEmployee = employeeController.createEmployee(employeeDto);

            assertThat(createdEmployee.organizationId()).isNull();
            verify(employeeService).createEmployee(employeeDto);
        }

    }

    @Nested
    class GetEmployeeTests {

        @Test
        void shouldGetEmployeeById() {
            EmployeeDto employeeDto = createEmployeeDto("John", "Doe", 0, 1L);
            given(employeeService.getEmployee(1L)).willReturn(employeeDto);

            EmployeeDto foundEmployee = employeeController.getEmployeeById(1L);

            assertThat(foundEmployee).isEqualTo(employeeDto);
            verify(employeeService).getEmployee(1L);
        }

        @Test
        void shouldReturnNotFoundForNonExistingEmployee() {
            given(employeeService.getEmployee(999L))
                    .willThrow(employeeNotFoundException.apply(999L));

            NoSuchElementException employeeNotFoundException = catchThrowableOfType(
                    () -> employeeController.getEmployeeById(999L),
                NoSuchElementException.class
            );

            assertThat(employeeNotFoundException.getMessage()).isEqualTo("Employee with id: 999 not found");
        }
    }

    @Nested
    class UpdateEmployeeTests {

        @Test
        void shouldUpdateEmployee() {
            EmployeeUpdateRequestDto updateRequest = createEmployeeUpdateRequestDto("Ryan", "Howard", 5, 1L);
            EmployeeDto updatedEmployee = createEmployeeDto("Ryan", "Howard", 5, 1L);
            given(employeeService.updateEmployee(1L, updateRequest)).willReturn(updatedEmployee);

            EmployeeDto result = employeeController.updateEmployee(1L, updateRequest);

            assertThat(result).isEqualTo(updatedEmployee);
            verify(employeeService).updateEmployee(1L, updateRequest);
        }

        @Test
        void shouldUpdateEmployeeWithNullOrganization() {
            EmployeeUpdateRequestDto updateRequest = createEmployeeUpdateRequestDto("Ryan", "Howard", 5, null);
            EmployeeDto updatedEmployee = createEmployeeDto("Ryan", "Howard", 5, null);
            given(employeeService.updateEmployee(1L, updateRequest)).willReturn(updatedEmployee);

            EmployeeDto result = employeeController.updateEmployee(1L, updateRequest);

            assertThat(result.organizationId()).isNull();
            verify(employeeService).updateEmployee(1L, updateRequest);
        }

    }

    @Nested
    class DeleteEmployeeTests {

        @Test
        void shouldDeleteEmployee() {
            employeeController.deleteEmployee(1L);
            verify(employeeService).deleteEmployee(1L);
        }

        @Test
        void shouldReturnNotFoundWhenDeletingNonExistingEmployee() {
            doThrow(employeeNotFoundException.apply(999L)).when(employeeService).deleteEmployee(999L);

        	NoSuchElementException employeeNotFoundException = catchThrowableOfType(
                    () -> employeeController.deleteEmployee(999L),
                NoSuchElementException.class
            );

            assertThat(employeeNotFoundException.getMessage()).isEqualTo("Employee with id: 999 not found");
        }
    }
}
