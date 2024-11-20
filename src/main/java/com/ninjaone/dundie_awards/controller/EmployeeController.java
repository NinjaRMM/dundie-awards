package com.ninjaone.dundie_awards.controller;

import java.util.List;
import java.util.Map;

import com.ninjaone.dundie_awards.events.MessageBroker;
import com.ninjaone.dundie_awards.dto.AwardDundieDTO;
import com.ninjaone.dundie_awards.dto.EmployeeRequestDTO;
import com.ninjaone.dundie_awards.dto.EmployeeResponseDTO;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping()
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    // get all employees
    @GetMapping("/employees")
    @ResponseBody
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    // create employee rest api
    @PostMapping("/employees")
    @ResponseBody
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody EmployeeRequestDTO employee) {

        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(employeeService.createEmployee(employee));
    }

    // get employee by id rest api
    @GetMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
       return ResponseEntity.ok(employeeService.findEmployee(id));
    }

    // update employee rest api
    @PutMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO employeeDetails) {

        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeDetails));
    }

    // delete employee rest api
    @DeleteMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(Map.of("DELETED",Boolean.TRUE));
    }

    @PatchMapping("/employees/{id}/award")
    public ResponseEntity<EmployeeResponseDTO> awardDundie(@PathVariable Long id, @RequestBody AwardDundieDTO awardDundieDTO) {
        return ResponseEntity.ok(employeeService.awardDundie(id, awardDundieDTO));
    }

}