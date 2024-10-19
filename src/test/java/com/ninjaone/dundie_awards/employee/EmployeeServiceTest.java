package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.error.NotFoundException;
import com.ninjaone.dundie_awards.organization.Organization;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static com.ninjaone.dundie_awards.generators.JpaDataGenerators.generateDbEmployee;
import static com.ninjaone.dundie_awards.generators.JpaDataGenerators.generateDbOrganization;
import static com.ninjaone.dundie_awards.infrastructure.DundieResource.EMPLOYEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@MockitoSettings
class EmployeeServiceTest {

    @Mock(answer = RETURNS_SMART_NULLS)
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Nested
    class GetEmployeeTests {
        @Test
        public void givenNoEmployeeFoundWhenFindingThenThrow() {
            long employeeId = 1;
            given(employeeRepository.findById(employeeId))
                    .willReturn(Optional.empty());

            NotFoundException exception = catchThrowableOfType(() -> employeeService.getEmployee(employeeId), NotFoundException.class);

            assertSoftly((softly) -> {
                softly.assertThat(exception.getId()).isEqualTo(employeeId);
                softly.assertThat(exception.getResource()).isEqualTo(EMPLOYEE);
            });
        }

        @Test
        public void givenEmployeeFoundWhenFindingThenReturn() {
            long employeeId = 1;
            Employee expected = generateDbEmployee();
            given(employeeRepository.findById(employeeId))
                    .willReturn(Optional.of(expected));

            Employee employee = employeeService.getEmployee(employeeId);

            assertThat(employee).isEqualTo(expected);
        }
    }

    @Nested
    class UpdateEmployeeTests {

        @Captor
        private ArgumentCaptor<Employee> employeeCaptor;

        @Test
        public void givenEmployeeNotFoundWhenUpdatingThenThrow() {
            long employeeId = 1;
            given(employeeRepository.findById(employeeId))
                    .willReturn(Optional.empty());

            NotFoundException exception = catchThrowableOfType(() ->
                            employeeService.updateEmployee(employeeId, generateDbEmployee()),
                    NotFoundException.class);

            assertSoftly((softly) -> {
                softly.assertThat(exception.getId()).isEqualTo(employeeId);
                softly.assertThat(exception.getResource()).isEqualTo(EMPLOYEE);
            });
        }

        @Test
        public void givenEmployeeFoundWhenUpdatingThenThrow() {
            long employeeId = 1;
            Employee employee = generateDbEmployee();
            Organization organization = generateDbOrganization();
            given(employeeRepository.findById(employeeId))
                    .willReturn(Optional.of(employee));
            given(employeeRepository.save(any()))
                    .willAnswer(it -> it.getArgument(0));
            Employee newEmployee = new Employee("New First", "New Last", organization);

            Employee result = employeeService.updateEmployee(employeeId, newEmployee);

            assertSoftly(softly -> {
                softly.assertThat(result.getFirstName()).isEqualTo(employee.getFirstName());
                softly.assertThat(result.getLastName()).isEqualTo(employee.getLastName());
                softly.assertThat(result.getOrganization()).isEqualTo(employee.getOrganization());
            });
        }
    }
}
