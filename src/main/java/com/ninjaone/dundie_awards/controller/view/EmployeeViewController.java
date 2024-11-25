package com.ninjaone.dundie_awards.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/employees")
@Controller
@RequiredArgsConstructor
public class EmployeeViewController {

	private final EmployeeService employeeService;

	@GetMapping("/view")
	public String viewEmployees(Model model) {
		model.addAttribute("employees", employeeService.getAllEmployees());
		return "employees :: content";
	}

}
