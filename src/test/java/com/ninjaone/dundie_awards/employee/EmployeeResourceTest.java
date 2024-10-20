package com.ninjaone.dundie_awards.employee;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void basicGetEmployeeTest() throws Exception {
        @Language("JSON")
        String expected = """
                    {
                      "firstName": "John",
                      "lastName": "Doe",
                      "dundieAwards": 0,
                      "organizationId": 1,
                      "organizationName": "Pikashu"
                    }
                """;

        mockMvc.perform(get("/employees/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    public void basicGetAllEmployeesTest() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(is("John")))
                .andExpect(jsonPath("$[0].lastName").value(is("Doe")))
                .andExpect(jsonPath("$[0].dundieAwards").value(is(0)))
                .andExpect(jsonPath("$[0].organizationId").value(is(1)))
                .andExpect(jsonPath("$[0].organizationName").value(is("Pikashu")))
        ;
    }

    @Test
    public void notFoundGetEmployeeTest() throws Exception {
        @Language("JSON")
        String expected = """
                {
                    "status": "NOT_FOUND",
                    "code": "not.found.exception",
                    "title": "The resource was not found",
                    "detail": "Resource 'Employee' with id '777' was not found"
                }
                """;

        mockMvc.perform(get("/employees/{id}", 777))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expected));
    }

    @Test
    public void basicCreateTest() throws Exception {
        @Language("JSON")
        String employee = """
                        {
                          "firstName": "Ash",
                          "lastName": "Ketchum",
                          "dundieAwards": null,
                          "organization": {
                            "id": 1,
                            "name": "Pikashu"
                          }
                        }
                """;

        @Language("JSON")
        String expected = """
                        {
                          "firstName": "Ash",
                          "lastName": "Ketchum",
                          "dundieAwards": 0,
                          "organizationId": 1,
                          "organizationName":  "Pikashu"
                        }
                """;
        mockMvc.perform(
                        post("/employees")
                                .content(employee)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(expected));
    }

    @Test
    public void basicUpdateTest() throws Exception {
        @Language("JSON")
        String employee = """
                {
                  "firstName": "Alpha",
                  "lastName": "Beta"
                }""";

        @Language("JSON")
        String expected = """
                {
                  "id": 1,
                  "firstName": "Alpha",
                  "lastName": "Beta",
                  "dundieAwards": 0,
                  "organizationId": 1,
                  "organizationName":  "Pikashu"
                }
                """;
        mockMvc.perform(
                        put("/employees/{id}", 1)
                                .content(employee)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    public void basicDeleteTest() throws Exception {
        mockMvc.perform(
                        delete("/employees/{id}", 2)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
