package com.ninjaone.dundie_awards.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/organizations")
@Controller
@RequiredArgsConstructor
public class OrganizationViewController {

	private final OrganizationService organizationService;

	@GetMapping("/view")
	public String viewOrganizations(Model model) {
		model.addAttribute("organizations", organizationService.getAllOrganizations());
		return "organizations :: content";
	}

}
