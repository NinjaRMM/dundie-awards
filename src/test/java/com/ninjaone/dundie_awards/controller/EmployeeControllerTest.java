package com.ninjaone.dundie_awards.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Arrays;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.controller.EmployeeController;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private AwardsCache awardsCache;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private MessageBroker messageBroker;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employee1 = Employee.builder().id(1L).firstName("John").lastName("Doe").build();
        employee2 = Employee.builder().id(2L).firstName("Jane").lastName("Doe").build();
    }

    @Test
    public void get_on_root_resource_should_produce_json_listing_of_all_employees() throws Exception {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    public void post_on_root_resource_should_save_a_new_employee() throws Exception {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employee1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void get_for_a_single_employee_url_when_employee_is_present_should_produce_single_employee_json() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void get_for_a_single_employee_url_when_employee_is_not_reprent_should_produce_404() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_to_single_employee_url_should_update_the_employee_in_repository_and_produce_new_json() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employee1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void put_to_single_employee_url_when_not_present_produces_a_404_response() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employee1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_to_single_employee_url_should_delete_from_repository_and_produces_a_200_status_response() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        doNothing().when(employeeRepository).delete(employee1);

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));

        verify(employeeRepository, times(1)).delete(employee1);
    }

    @Test
    public void delete_to_single_employee_url_when_not_present_produces_a_404_response() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNotFound());
    }
}