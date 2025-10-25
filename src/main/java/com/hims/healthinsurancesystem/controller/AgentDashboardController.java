package com.hims.healthinsurancesystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AgentDashboardController {

    @GetMapping("/agent/dashboard")
    public String showDashboard(Model model) {
        // Add any data needed by the dashboard page
        // model.addAttribute("plans", someService.getPlans());
        return "agent-dashboard";  // <-- matches templates/insurance-dashboard.html
    }
}
