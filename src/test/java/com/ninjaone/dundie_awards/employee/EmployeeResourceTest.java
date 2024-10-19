package com.ninjaone.dundie_awards.employee;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeResourceTest {

    @Autowired
    private MockMvc mockMvc;

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
                          "organization": {
                            "id": 1,
                            "name": "Pikashu"
                          }
                        }
                """;
        mockMvc.perform(
                        post("/employees")
                                .content(employee)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
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
                  "dundieAwards": null,
                  "organization": {
                    "id": 1,
                    "name": "Pikashu"
                  }
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
        @Language("JSON")
        String expected = """
                {
                  "deleted": true
                }
                """;

        mockMvc.perform(
                        delete("/employees/{id}", 2)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }
}
