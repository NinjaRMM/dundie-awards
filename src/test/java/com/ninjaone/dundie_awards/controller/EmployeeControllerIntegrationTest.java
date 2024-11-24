package com.ninjaone.dundie_awards.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String createEmployeeJson(String firstName, String lastName, int dundieAwards, long organizationId) {
        return String.format("""
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "dundieAwards": %d,
                  "organization": {
                    "id": %d
                  }
                }
                """, firstName, lastName, dundieAwards, organizationId);
    }

    @Nested
    class CreateEmployeeTests {

        @Test
        public void shouldCreateEmployee() throws Exception {
            String employee = createEmployeeJson("Ryan", "Howard", 0, 1);

            mockMvc.perform(
                    post("/employees")
                            .content(employee)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("firstName", is("Ryan")))
                    .andExpect(jsonPath("lastName", is("Howard")))
                    .andExpect(jsonPath("dundieAwards", is(0)))
                    .andExpect(jsonPath("organization.id", is(1)));
        }

        @Test
        public void shouldReturnBadRequestForInvalidOrganizationId() throws Exception {
            String invalidEmployee = createEmployeeJson("Ryan", "Howard", 0, 999);

            mockMvc.perform(
                    post("/employees")
                            .content(invalidEmployee)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", equalTo("Invalid organization with id: 999. Organization not found")));
        }
        
        @Test
        public void shouldCreateEmployeeWithNullOrganization() throws Exception {
            String employeeWithNullOrganization = """
                {
                  "firstName": "Ryan",
                  "lastName": "Howard",
                  "dundieAwards": 0,
                  "organization": null
                }
                """;

            mockMvc.perform(
                    post("/employees")
                            .content(employeeWithNullOrganization)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("organization").doesNotExist());
        }

        @Test
        public void shouldReturnBadRequestForNullOrganizationId() throws Exception {
            String employeeWithNullOrgId = """
                {
                  "firstName": "Ryan",
                  "lastName": "Howard",
                  "dundieAwards": 0,
                  "organization": { "id": null }
                }
                """;

            mockMvc.perform(
                    post("/employees")
                            .content(employeeWithNullOrgId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", equalTo("The provided organization ID cannot be null.")));
        }
    }

    @Nested
    class GetEmployeeTests {

        @Test
        public void shouldGetEmployeeById() throws Exception {
            mockMvc.perform(get("/employees/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("firstName", is("John")))
                    .andExpect(jsonPath("lastName", is("Doe")))
                    .andExpect(jsonPath("dundieAwards",nullValue()))
                    .andExpect(jsonPath("organization.id", is(1)))
                    .andExpect(jsonPath("organization.name", is("Pikashu")));
        }

        @Test
        public void shouldReturnNotFoundForNonExistingEmployee() throws Exception {
            mockMvc.perform(get("/employees/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.detail", equalTo("Employee with id: 999 not found")));
        }
    }

    @Nested
    class UpdateEmployeeTests {

        @Test
        public void shouldUpdateEmployee() throws Exception {
            String updatedEmployee = createEmployeeJson("Ryan", "Howard", 5, 1);

            mockMvc.perform(
                    put("/employees/{id}", 1)
                            .content(updatedEmployee)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("firstName", is("Ryan")))
                    .andExpect(jsonPath("lastName", is("Howard")))
                    .andExpect(jsonPath("dundieAwards", is(5)))
                    .andExpect(jsonPath("organization.id", is(1)))
                    .andExpect(jsonPath("organization.name", is("Pikashu")));
        }
        
        @Test
        public void shouldReturnBadRequestForInvalidOrganizationId() throws Exception {
            String invalidUpdatedEmployee = createEmployeeJson("Ryan", "Howard", 5, 999);

            mockMvc.perform(
                    put("/employees/{id}", 1)
                            .content(invalidUpdatedEmployee)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", equalTo("Invalid organization with id: 999. Organization not found")));
        }
        
        @Test
        public void shouldUpdateEmployeeWithNullOrganization() throws Exception {
            String updatedEmployeeWithNullOrg = """
                {
                  "firstName": "Ryan",
                  "lastName": "Howard",
                  "dundieAwards": 5,
                  "organization": null
                }
                """;

            mockMvc.perform(
                    put("/employees/{id}", 1)
                            .content(updatedEmployeeWithNullOrg)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("organization").doesNotExist());
        }

        @Test
        public void shouldReturnBadRequestForNullOrganizationIdOnUpdate() throws Exception {
            String updatedEmployeeWithNullOrgId = """
                {
                  "firstName": "Ryan",
                  "lastName": "Howard",
                  "dundieAwards": 5,
                  "organization": { "id": null }
                }
                """;

            mockMvc.perform(
                    put("/employees/{id}", 1)
                            .content(updatedEmployeeWithNullOrgId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", equalTo("The provided organization ID cannot be null.")));
        }
    }

    @Nested
    class DeleteEmployeeTests {

        @Test
        public void shouldDeleteEmployee() throws Exception {
            mockMvc.perform(delete("/employees/{id}", 2))
                    .andExpect(status().isNoContent());
        }

        @Test
        public void shouldReturnNotFoundWhenDeletingNonExistingEmployee() throws Exception {
            mockMvc.perform(delete("/employees/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.detail", equalTo("Employee with id: 999 not found")));
        }
    }
}
