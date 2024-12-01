package com.ninjaone.dundie_awards.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequestMapping("/modal")
@Controller
@RequiredArgsConstructor
public class ModalViewController {

	@GetMapping("/view")
	public String viewOrganizations(Model model) {
		return "modal :: content";
	}

}
