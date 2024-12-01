package com.ninjaone.dundie_awards.controller.view;

import java.text.DecimalFormat;

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
	private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @GetMapping("/view")
    public String getActivities(Model model) {
        String formattedAwards = decimalFormat.format(awardsCache.getTotalAwards());
        model.addAttribute("totalDundieAwards", formattedAwards);
        return "awards :: content";
    }

}
