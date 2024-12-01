package com.ninjaone.dundie_awards.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/employees")
@Controller
@RequiredArgsConstructor
public class EmployeeViewController {

	private final EmployeeService employeeService;

	@GetMapping("/view")
	public String viewEmployees(Model model) {
		List<EmployeeDto> allEmployees = employeeService.getAllEmployees();
		model.addAttribute("employees", allEmployees.isEmpty()?null:allEmployees);
		return "employees :: content";
	}

}
