package com.ninjaone.dundie_awards.service;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.invalidIdException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private Employee createEmployee(String firstName, String lastName, int dundieAwards, Long organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDundieAwards(dundieAwards);
        employee.setOrganization(organization);
        return employee;
    }

    @Nested
    class CreateEmployeeTests {

        @Test
        void shouldCreateEmployee() {
            Employee employee = createEmployee("Ryan", "Howard", 0, 1L);
            given(organizationService.getValidOrganization(1L)).willReturn(employee.getOrganization());
            given(employeeRepository.save(employee)).willReturn(employee);

            Employee createdEmployee = employeeService.createEmployee(employee);

            assertThat(createdEmployee).isEqualTo(employee);
            verify(organizationService).ensureValidOrganization(1L);
            verify(employeeRepository).save(employee);
        }

        @Test
        void shouldCreateEmployeeWithNullOrganization() {
            Employee employee = createEmployee("Ryan", "Howard", 0, null);
            employee.setOrganization(null);
            given(employeeRepository.save(employee)).willReturn(employee);

            Employee createdEmployee = employeeService.createEmployee(employee);

            assertThat(createdEmployee.getOrganization()).isNull();
            verify(employeeRepository).save(employee);
        }
        
        @Test
        void shouldReturnBadRequestForInvalidOrganizationId() {
            Employee invalidEmployee = createEmployee("Ryan", "Howard", 0, 999L);

            doThrow(new IllegalArgumentException("Invalid organization with id: 999. Organization not found"))
                .when(organizationService).ensureValidOrganization(999L);

            IllegalArgumentException exception = catchThrowableOfType(
                () -> employeeService.createEmployee(invalidEmployee),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }

        @Test
        void shouldReturnBadRequestForNullOrganizationId() {
            Employee invalidEmployee = createEmployee("Ryan", "Howard", 0, null);

            doThrow(invalidIdException.get())
            	.when(organizationService).ensureValidOrganization(null);

            IllegalArgumentException exception = catchThrowableOfType(
                () -> employeeService.createEmployee(invalidEmployee),
                IllegalArgumentException.class
            );

            assertThat(exception.getMessage()).isEqualTo("The provided organization ID cannot be null.");
        }
    }

    @Nested
    class GetEmployeeTests {

        @Test
        void shouldGetEmployeeById() {
            Employee employee = createEmployee("John", "Doe", 0, 1L);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

            Employee foundEmployee = employeeService.getEmployee(1L);

            assertThat(foundEmployee).isEqualTo(employee);
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
            Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1L);
            Employee updatedDetails = createEmployee("Ryan", "Howard", 5, 1L);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
            given(organizationService.getValidOrganization(1L)).willReturn(updatedDetails.getOrganization());
            given(employeeRepository.save(existingEmployee)).willReturn(existingEmployee);

            Employee updatedEmployee = employeeService.updateEmployee(1L, updatedDetails);

            assertThat(updatedEmployee.getDundieAwards()).isEqualTo(5);
            verify(employeeRepository).findById(1L);
            verify(employeeRepository).save(existingEmployee);
        }

        @Test
        void shouldUpdateEmployeeWithNullOrganization() {
            Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1L);
            Employee updatedDetails = createEmployee("Ryan", "Howard", 5, null);
            updatedDetails.setOrganization(null);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
            given(employeeRepository.save(existingEmployee)).willReturn(existingEmployee);

            Employee updatedEmployee = employeeService.updateEmployee(1L, updatedDetails);

            assertThat(updatedEmployee.getOrganization()).isNull();
            verify(employeeRepository).findById(1L);
            verify(employeeRepository).save(existingEmployee);
        }
        
        @Test
        void shouldReturnBadRequestForInvalidOrganizationIdOnUpdate() {
            Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1L);
            Employee invalidUpdatedEmployee = createEmployee("Ryan", "Howard", 5, 999L);

            given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
            doThrow(new IllegalArgumentException("Invalid organization with id: 999. Organization not found"))
                .when(organizationService).getValidOrganization(999L);

            IllegalArgumentException exception = catchThrowableOfType(
                () -> employeeService.updateEmployee(1L, invalidUpdatedEmployee),
                IllegalArgumentException.class
            );

            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }


        @Test
        void shouldReturnBadRequestForNullOrganizationIdOnUpdate() {
        	Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1L);
            Employee invalidEmployee = createEmployee("Ryan", "Howard", 5, null);
            invalidEmployee.setOrganization(new Organization());
            given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
            doThrow(invalidIdException.get())
        		.when(organizationService).getValidOrganization(null);

            IllegalArgumentException  exception = catchThrowableOfType(
                () -> employeeService.updateEmployee(1L, invalidEmployee),
                IllegalArgumentException .class
            );

            assertThat(exception.getMessage()).isEqualTo("The provided organization ID cannot be null.");
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
