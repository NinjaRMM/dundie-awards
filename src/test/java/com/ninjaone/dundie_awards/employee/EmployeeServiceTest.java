package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.error.NotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static com.ninjaone.dundie_awards.employee.EmployeeRecordTest.assertEmployeeRecordEqualsDb;
import static com.ninjaone.dundie_awards.generators.JpaDataGenerators.generateDbEmployee;
import static com.ninjaone.dundie_awards.infrastructure.DundieResource.EMPLOYEE;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
            Employee expected = generateDbEmployee();
            long employeeId = expected.getId();
            given(employeeRepository.findById(employeeId))
                    .willReturn(Optional.of(expected));

            EmployeeRecord employee = employeeService.getEmployee(employeeId);

            assertEmployeeRecordEqualsDb(employee, expected);
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
            UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                    .lastName("Genero")
                    .build();

            NotFoundException exception = catchThrowableOfType(() ->
                            employeeService.updateEmployee(employeeId,
                                    request),
                    NotFoundException.class);

            assertSoftly((softly) -> {
                softly.assertThat(exception.getId()).isEqualTo(employeeId);
                softly.assertThat(exception.getResource()).isEqualTo(EMPLOYEE);
            });
        }

        @Test
        public void givenEmployeeFoundWhenUpdatingThenThrow() {
            Employee employee = generateDbEmployee();
            long employeeId = employee.getId();
            given(employeeRepository.findById(employeeId))
                    .willReturn(Optional.of(employee));
            // TODO 2024-10-20 Dom - Test more scenarios
            String newLastName = "Genero";
            UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                    .lastName(newLastName)
                    .build();

            EmployeeRecord result = employeeService.updateEmployee(employeeId, request);

            assertSoftly(softly -> {
                softly.assertThat(result.firstName()).isEqualTo(employee.getFirstName());
                softly.assertThat(result.lastName()).isEqualTo(newLastName);
                softly.assertThat(result.lastName()).isEqualTo(employee.getLastName());
                softly.assertThat(result.dundieAwards()).isEqualTo(employee.getDundieAwards());
                softly.assertThat(result.organizationId()).isEqualTo(employee.getOrganization().getId());
                softly.assertThat(result.organizationName()).isEqualTo(employee.getOrganization().getName());
            });
        }
    }

    @Nested
    class DeleteEmployeeTests {
        @Test
        public void shouldDeleteEmployee() {
            long employeeId = 1;

            employeeService.deleteEmployee(employeeId);

            verify(employeeRepository).deleteById(employeeId);
        }
    }
}
