package com.ninjaone.dundie_awards.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void builder_based_creation_should_function_properly() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .dundieAwards(5)
                .build();

        assertThat(employee, notNullValue());
        assertThat(employee.getFirstName(), is("John"));
        assertThat(employee.getLastName(), is("Doe"));
        assertThat(employee.getDundieAwards(), is(5));
    }

    @Test
    void no_arg_constructor_should_function_properly() {
        Employee employee = new Employee();
        assertThat(employee, notNullValue());
    }

    @Test
    void all_args_constructor_should_function_properly() {
        Organization org = new Organization();
        Employee employee = new Employee(1L, "Jane", "Doe", 3, org);

        assertThat(employee, notNullValue());
        assertThat(employee.getId(), is(1L));
        assertThat(employee.getFirstName(), is("Jane"));
        assertThat(employee.getLastName(), is("Doe"));
        assertThat(employee.getDundieAwards(), is(3));
        assertThat(employee.getOrganization(), is(org));
        assertThat(employee.toString(), containsString("Jane"));
    }

    @Test
    void hashcode_and_equals_should_function_properly() {
        Organization org = new Organization();
        Employee employee1 = new Employee(1L, "Jane", "Doe", 3, org);
        Employee employee2 = new Employee(1L, "Jane", "Doe", 3, org);
        Employee employee3 = new Employee(1L, "John", "Doe", 1, org);
        Employee employee4 = new Employee(2L, "John", "Doe", 1, org);
        assertThat(employee1, is(employee2));
        assertThat(employee1, not(employee3));
        assertThat(employee4, not(employee3));
        assertThat(employee1.hashCode(), is(employee2.hashCode()));
        assertThat(employee1.hashCode(), not(employee3.hashCode()));
        assertThat(employee4.hashCode(), not(employee1.hashCode()));
    }

    @Test
    void getters_and_setters_should_function_properly() {
        Employee employee = new Employee();
        employee.setId(2L);
        employee.setFirstName("Jim");
        employee.setLastName("Halpert");
        employee.setDundieAwards(7);
        Organization org = new Organization();
        employee.setOrganization(org);

        assertThat(employee.getId(), is(2L));
        assertThat(employee.getFirstName(), is("Jim"));
        assertThat(employee.getLastName(), is("Halpert"));
        assertThat(employee.getDundieAwards(), is(7));
        assertThat(employee.getOrganization(), is(org));
    }
}