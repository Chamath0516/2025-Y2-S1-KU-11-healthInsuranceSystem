package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.InsurancePlanService;
import com.hims.healthinsurancesystem.model.InsurancePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class PlansController {

    @Autowired
    private InsurancePlanService insurancePlanService;

    @GetMapping("/plans")
    public String publicPlans(Model model) {
        List<InsurancePlan> plans = insurancePlanService.findAll();
        model.addAttribute("plans", plans);
        return "plans-public";
    }

    @GetMapping("/plans/compare")
    public String comparePlans(@RequestParam(name = "ids", required = false) List<Long> ids, Model model) {
        List<InsurancePlan> plans = ids == null || ids.isEmpty() ? Collections.emptyList() : insurancePlanService.findAllById(ids);
        model.addAttribute("plans", plans);
        return "compare-plans";
    }
}
