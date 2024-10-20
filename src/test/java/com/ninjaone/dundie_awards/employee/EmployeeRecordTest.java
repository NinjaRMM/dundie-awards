package com.ninjaone.dundie_awards.employee;

import org.junit.jupiter.api.Test;

import static com.ninjaone.dundie_awards.generators.JpaDataGenerators.generateDbEmployee;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EmployeeRecordTest {
    @Test
    public void shouldCheckAllValuesAreCopied() {
        Employee source = generateDbEmployee();

        EmployeeRecord result = EmployeeRecord.fromDb(source);

        assertEmployeeRecordEqualsDb(result, source);
    }

    public static void assertEmployeeRecordEqualsDb(EmployeeRecord result, Employee source) {
        assertSoftly(softly -> {
            softly.assertThat(result.firstName()).isEqualTo(source.getFirstName());
            softly.assertThat(result.lastName()).isEqualTo(source.getLastName());
            softly.assertThat(result.dundieAwards()).isEqualTo(source.getDundieAwards());
            softly.assertThat(result.organizationId()).isEqualTo(source.getOrganization().getId());
            softly.assertThat(result.organizationName()).isEqualTo(source.getOrganization().getName());
            softly.assertThat(result).usingRecursiveAssertion().hasNoNullFields();
        });
    }
}
