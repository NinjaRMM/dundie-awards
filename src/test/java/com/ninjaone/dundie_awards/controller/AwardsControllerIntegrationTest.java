package com.ninjaone.dundie_awards.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AwardsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    

    @Test
    void shouldGiveDundieAwardsToOrganization() throws Exception {
        mockMvc.perform(post("/give-dundie-awards/{organizationId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundForNonExistingOrganization() throws Exception {
        mockMvc.perform(post("/give-dundie-awards/{organizationId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", is("Organization with id: 999 not found")))
                .andExpect(jsonPath("$.instance", is("/give-dundie-awards/999")))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    
    @Test
    void shouldReturnBadRequestForBlockedOrganization() throws Exception {
    	Organization blockedOrganization = Organization.builder()
        		.id(3L)
        		.blocked(true)
        		.build();
        organizationRepository.save(blockedOrganization);
        
        mockMvc.perform(post("/give-dundie-awards/{organizationId}", 3L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Validation failure")))
                .andExpect(jsonPath("$.errors[0].field", is("method 'giveOrganizationDundieAwards' parameter 0")))
                .andExpect(jsonPath("$.errors[0].message", is("The organization is blocked and cannot perform this operation.")));
    }
}
