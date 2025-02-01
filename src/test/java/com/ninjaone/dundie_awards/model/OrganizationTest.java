package com.ninjaone.dundie_awards.model;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;


class OrganizationTest {

    @Test
    void builder_based_creation_should_function_properly() {
        Organization organization = Organization.builder()
                .id(1L)
                .name("NinjaOne")
                .build();

        assertThat(organization, notNullValue());
        assertThat(organization.getId(), is(1L));
        assertThat(organization.getName(), is("NinjaOne"));
    }

    @Test
    void no_arg_constructor_should_function_properly() {
        Organization organization = new Organization();
        assertThat(organization, notNullValue());
    }

    @Test
    void all_args_constructor_should_function_properly() {
        Organization organization = new Organization(1L, "NinjaOne");
        assertThat(organization, notNullValue());
        assertThat(organization.getId(), is(1L));
        assertThat(organization.getName(), is("NinjaOne"));
    }

    @Test
    void equals_and_hashcode_should_function_properly() {
        Organization org1 = new Organization(1L, "NinjaOne");
        Organization org2 = new Organization(1L, "NinjaOne");
        Organization org3 = new Organization(2L, "NinjaTwo");
        Organization org4 = new Organization(3L, "NinjaOne");

        assertThat(org2, is(org1));
        assertThat(org3, not(org1));
        assertThat(org4, not(org1));
        assertThat(org2.hashCode(), is(org1.hashCode()));
        assertThat(org3.hashCode(), not(org1.hashCode()));
        assertThat(org4.hashCode(), not(org1.hashCode()));
    }
}