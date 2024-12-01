package com.ninjaone.dundie_awards.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.dto.OrganizationDto;
import com.ninjaone.dundie_awards.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/organizations")
@Controller
@RequiredArgsConstructor
public class OrganizationViewController {

	private final OrganizationService organizationService;
	private final AppProperties appProperties;

	@GetMapping("/view")
	public String viewOrganizations(Model model) {
		model.addAttribute("enableTestBehavior", appProperties.enableTestBehavior());
		List<OrganizationDto> organizations = organizationService.getAllOrganizations();
		model.addAttribute("organizations", organizations.isEmpty()?null:organizations);
		return "organizations :: content";
	}

}
