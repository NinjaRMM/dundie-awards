package com.ninjaone.dundie_awards.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.AwardsCache;

import lombok.RequiredArgsConstructor;

@RequestMapping("/awards")
@Controller
@RequiredArgsConstructor
public class AwardsViewController {
    
	private final AwardsCache awardsCache;

    @GetMapping("/view")
    public String getActivities(Model model) {
        model.addAttribute("totalDundieAwards", awardsCache.getTotalAwards());
        return "awards :: content";
    }

}
