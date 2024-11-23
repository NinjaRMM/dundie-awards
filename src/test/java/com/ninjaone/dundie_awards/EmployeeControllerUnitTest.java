package com.ninjaone.dundie_awards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.ErrorResponseException;

import com.ninjaone.dundie_awards.controller.EmployeeController;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

class EmployeeControllerUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Employee createEmployee(String firstName, String lastName, int dundieAwards, long organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);
        Employee employee = new Employee(firstName, lastName, organization);
        employee.setDundieAwards(dundieAwards);
        return employee;
    }

    @Nested
    class CreateEmployeeTests {

        @Test
        void shouldCreateEmployee() {
            Employee employee = createEmployee("Ryan", "Howard", 0, 1);
            given(organizationRepository.existsById(1L)).willReturn(true);
            given(employeeRepository.save(employee)).willReturn(employee);

            Employee createdEmployee = employeeController.createEmployee(employee);

            assertThat(createdEmployee).isEqualTo(employee);
            verify(employeeRepository).save(employee);
        }

        @Test
        void shouldReturnBadRequestForInvalidOrganizationId() {
            Employee invalidEmployee = createEmployee("Ryan", "Howard", 0, 999);
            given(organizationRepository.existsById(999L)).willReturn(false);

            ErrorResponseException organizationNotValidException = catchThrowableOfType(
                () -> employeeController.createEmployee(invalidEmployee),
                ErrorResponseException.class
            );

            assertThat(organizationNotValidException.getBody().getDetail()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }
    }

    @Nested
    class GetEmployeeTests {

        @Test
        void shouldGetEmployeeById() {
            Employee employee = createEmployee("John", "Doe", 0, 1);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

            Employee foundEmployee = employeeController.getEmployeeById(1L);

            assertThat(foundEmployee).isEqualTo(employee);
        }

        @Test
        void shouldReturnNotFoundForNonExistingEmployee() {
            given(employeeRepository.findById(999L)).willReturn(Optional.empty());

            ErrorResponseException employeeNotFoundException = catchThrowableOfType(
                () -> employeeController.getEmployeeById(999L),
                ErrorResponseException.class
            );

            assertThat(employeeNotFoundException.getBody().getDetail()).isEqualTo("Employee with id: 999 not found");
        }
    }

    @Nested
    class UpdateEmployeeTests {

        @Test
        void shouldUpdateEmployee() {
            Employee existingEmployee = createEmployee("Ryan", "Howard", 0, 1);
            Employee updatedDetails = createEmployee("Ryan", "Howard", 5, 1);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(existingEmployee));
            given(organizationRepository.findById(1L)).willReturn(Optional.of(existingEmployee.getOrganization()));
            given(employeeRepository.save(existingEmployee)).willReturn(existingEmployee);

            Employee updatedEmployee = employeeController.updateEmployee(1L, updatedDetails);

            assertThat(updatedEmployee.getDundieAwards()).isEqualTo(5);
            verify(employeeRepository).save(existingEmployee);
        }

        @Test
        void shouldReturnBadRequestForInvalidOrganizationId() {
            Employee invalidDetails = createEmployee("Ryan", "Howard", 5, 999);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(invalidDetails));
            given(organizationRepository.findById(999L)).willReturn(Optional.empty());

            ErrorResponseException organizationNotValidException = catchThrowableOfType(
                () -> employeeController.updateEmployee(1L, invalidDetails),
                ErrorResponseException.class
            );

            assertThat(organizationNotValidException.getBody().getDetail()).isEqualTo("Invalid organization with id: 999. Organization not found");
        }
    }

    @Nested
    class DeleteEmployeeTests {

        @Test
        void shouldDeleteEmployee() {
            Employee employee = createEmployee("Ryan", "Howard", 0, 1);
            given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

            employeeController.deleteEmployee(1L);

            verify(employeeRepository).delete(employee);
        }

        @Test
        void shouldReturnNotFoundWhenDeletingNonExistingEmployee() {
            given(employeeRepository.findById(999L)).willReturn(Optional.empty());

            ErrorResponseException employeeNotFoundException = catchThrowableOfType(
                () -> employeeController.deleteEmployee(999L),
                ErrorResponseException.class
            );

            assertThat(employeeNotFoundException.getBody().getDetail()).isEqualTo("Employee with id: 999 not found");
        }
    }
}
