package com.hims.healthinsurancesystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";  // Points to src/main/resources/templates/home.html
    }

    @GetMapping("/about")
    public String about() {
        return "about-us";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact-us";
    }
}