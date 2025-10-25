package web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support")
public class SupportController {

    @GetMapping("/tickets")
    public String viewTickets(Model model) {
        // In absence of a service, ensure the template can render with an empty list
        model.addAttribute("tickets", java.util.Collections.emptyList());
        // Optionally provide a username if template expects it from dashboard redirects
        model.addAttribute("username", "Support Officer");
        return "support-tickets"; // resolves to templates/support-tickets.html
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("username", "Support Officer");
        return "support-dashboard";
    }
}

