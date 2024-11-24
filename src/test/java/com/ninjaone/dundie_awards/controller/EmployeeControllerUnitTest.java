package com.ninjaone.dundie_awards.controller;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.employeeNotFoundException;
import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.notValidException;
import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.organizationNotValidException;
import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployee;
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
import org.springframework.web.ErrorResponseException;

import com.ninjaone.dundie_awards.model.Employee;
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
            Employee employee = createEmployee("Ryan", "Howard", 0, 1L);
            given(employeeService.createEmployee(employee)).willReturn(employee);

            Employee createdEmployee = employeeController.createEmployee(employee);

            assertThat(createdEmployee).isEqualTo(employee);
            verify(employeeService).createEmployee(employee);
        }
        
        @Test
        void shouldCreateEmployeeWithNullOrganization() {
            Employee employee = createEmployee("Ryan", "Howard", 0, 0L);
            employee.setOrganization(null);
            given(employeeService.createEmployee(employee)).willReturn(employee);

            Employee createdEmployee = employeeController.createEmployee(employee);

            assertThat(createdEmployee.getOrganization()).isNull();
            verify(employeeService).createEmployee(employee);
        }

        @Test
        void shouldReturnBadRequestForNullOrganizationId() {
            Employee employeeWithNullOrgId = createEmployee("Ryan", "Howard", 0, 0L);
            employeeWithNullOrgId.setOrganization(employeeWithNullOrgId.getOrganization().toBuilder().id(null).build());

            given(employeeService.createEmployee(employeeWithNullOrgId))
                    .willThrow(notValidException.apply("The provided organization ID cannot be null."));

            ErrorResponseException invalidOrganizationException = catchThrowableOfType(
                    () -> employeeController.createEmployee(employeeWithNullOrgId),
                    ErrorResponseException.class
            );

            assertThat(invalidOrganizationException.getBody().getDetail()).isEqualTo("The provided organization ID cannot be null.");
        }

        @Test
        void shouldReturnBadRequestForInvalidOrganizationId() {
            Employee invalidEmployee = createEmployee("Ryan", "Howard", 0, 999L);
            given(employeeService.createEmployee(invalidEmployee))
                    .willThrow(organizationNotValidException.apply(invalidEmployee.getOrganization().getId()));

            IllegalArgumentException organizationNotValidException = catchThrowableOfType(
                () -> employeeController.createEmployee(invalidEmployee),
                IllegalArgumentException.class
            );

            assertThat(organizationNotValidException.getMessage()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }
    }

    @Nested
    class GetEmployeeTests {

        @Test
        void shouldGetEmployeeById() {
            Employee employee = createEmployee("John", "Doe", 0, 1L);
            given(employeeService.getEmployee(1L)).willReturn(employee);

            Employee foundEmployee = employeeController.getEmployeeById(1L);

            assertThat(foundEmployee).isEqualTo(employee);
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
            Employee updatedDetails = createEmployee("Ryan", "Howard", 5, 1L);
            Employee updatedEmployee = createEmployee("Ryan", "Howard", 5, 1L); 
            given(employeeService.updateEmployee(1L, updatedDetails)).willReturn(updatedEmployee);

            Employee result = employeeController.updateEmployee(1L, updatedDetails);

            assertThat(result.getDundieAwards()).isEqualTo(5);
            verify(employeeService).updateEmployee(1L, updatedDetails);
        }
        
        @Test
        void shouldUpdateEmployeeWithNullOrganization() {
            Employee employeeToUpdate = createEmployee("Ryan", "Howard", 5, 0L);
            employeeToUpdate.setOrganization(null);
            Employee updatedEmployee = createEmployee("Ryan", "Howard", 5, 0L);
            updatedEmployee.setOrganization(null);

            given(employeeService.updateEmployee(1L, employeeToUpdate)).willReturn(updatedEmployee);

            Employee result = employeeController.updateEmployee(1L, employeeToUpdate);

            assertThat(result.getOrganization()).isNull();
            verify(employeeService).updateEmployee(1L, employeeToUpdate);
        }


        @Test
        void shouldReturnBadRequestForInvalidOrganizationId() {
            Employee invalidDetails = createEmployee("Ryan", "Howard", 5, 999L);
            given(employeeService.updateEmployee(1L, invalidDetails))
                    .willThrow(organizationNotValidException.apply(999L));

            IllegalArgumentException organizationNotValidException = catchThrowableOfType(
                () -> employeeController.updateEmployee(1L, invalidDetails),
                IllegalArgumentException.class
            );

            assertThat(organizationNotValidException.getMessage()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }
        
        @Test
        void shouldReturnBadRequestForNullOrganizationIdOnUpdate() {
            Employee employeeWithNullOrgId = createEmployee("Ryan", "Howard", 5, 0L);
            employeeWithNullOrgId.setOrganization(employeeWithNullOrgId.getOrganization().toBuilder().id(null).build());

            given(employeeService.updateEmployee(1L, employeeWithNullOrgId))
                    .willThrow(notValidException.apply("The provided organization ID cannot be null."));

            ErrorResponseException invalidOrganizationException = catchThrowableOfType(
                    () -> employeeController.updateEmployee(1L, employeeWithNullOrgId),
                    ErrorResponseException.class
            );

            assertThat(invalidOrganizationException.getBody().getDetail()).isEqualTo("The provided organization ID cannot be null.");
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
