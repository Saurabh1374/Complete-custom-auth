package com.kitchome.auth.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	@GetMapping("/")
	public String index(Model model) {
		
		model.addAttribute("loginLink", "api/v1/users/dashboard");
		model.addAttribute("registerLink", "api/v1/users/register");

		return "Index"; // Name of the Thymeleaf template (welcome.html)
	}

}
