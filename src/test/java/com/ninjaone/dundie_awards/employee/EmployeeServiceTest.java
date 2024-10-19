package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static com.ninjaone.dundie_awards.generators.JpaDataGenerators.generateDbEmployee;
import static com.ninjaone.dundie_awards.infrastructure.DundieResource.EMPLOYEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.BDDMockito.given;

@MockitoSettings
class EmployeeServiceTest {

    @Mock(answer = RETURNS_SMART_NULLS)
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    public void givenNoEmployeeFoundThenThrow() {
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
    public void givenEmployeeFoundThenReturn() {
        long employeeId = 1;
        Employee expected = generateDbEmployee();
        given(employeeRepository.findById(employeeId))
                .willReturn(Optional.of(expected));

        Employee employee = employeeService.getEmployee(employeeId);

        assertThat(employee).isEqualTo(expected);
    }
}
