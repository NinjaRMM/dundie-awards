package com.ninjaone.dundie_awards.controller;

import static com.ninjaone.dundie_awards.util.TestEntityFactory.createEmployeeJson;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
    

    @Nested
    class CreateEmployeeTests {

        @Test
        public void shouldCreateEmployee() throws Exception {
            String employee = createEmployeeJson("Ryan", "Howard", 0, 1L);

            mockMvc.perform(
                    post("/employees")
                            .content(employee)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("firstName", is("Ryan")))
                    .andExpect(jsonPath("lastName", is("Howard")))
                    .andExpect(jsonPath("dundieAwards", is(0)))
                    .andExpect(jsonPath("organizationId", is(1)));
        }

        @Test
        public void shouldReturnBadRequestForInvalidOrganizationId() throws Exception {
            String invalidEmployee = createEmployeeJson("Ryan", "Howard", 0, 999L);

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

    }

    @Nested
    class GetEmployeeTests {

        @Test
        public void shouldGetEmployeeById() throws Exception {
            mockMvc.perform(get("/employees/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("firstName", is("John")))
                    .andExpect(jsonPath("lastName", is("Doe")))
                    .andExpect(jsonPath("dundieAwards",is(0)))
                    .andExpect(jsonPath("organizationId", is(1)))
                    .andExpect(jsonPath("organizationName", is("Pikashu")));
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
            String updatedEmployee = createEmployeeJson("Ryan", "Howard", 5, 1L);

            mockMvc.perform(
                    put("/employees/{id}", 1)
                            .content(updatedEmployee)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("firstName", is("Ryan")))
                    .andExpect(jsonPath("lastName", is("Howard")))
                    .andExpect(jsonPath("dundieAwards", is(5)))
                    .andExpect(jsonPath("organizationId", is(1)))
                    .andExpect(jsonPath("organizationName", is("Pikashu")));
        }
        
        @Test
        public void shouldReturnBadRequestForInvalidOrganizationId() throws Exception {
            String invalidUpdatedEmployee = createEmployeeJson("Ryan", "Howard", 5, 999L);

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
