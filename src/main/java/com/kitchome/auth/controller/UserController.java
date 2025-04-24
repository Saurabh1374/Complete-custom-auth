package com.kitchome.auth.controller;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.kitchome.auth.payload.RegisterUserDTO;
import com.kitchome.auth.service.UserService;

@Controller
@RequestMapping("api/v1/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	 @GetMapping("/register")
	    public String showRegistrationForm() {
	        return "register"; // This will look for register.html in resources/templates
	    }
	@PostMapping("/register")
	public ResponseEntity<?> createUser(@RequestBody RegisterUserDTO userDto) {
		userService.registerUser(userDto);
		 return ResponseEntity
	                .status(HttpStatus.CREATED)
	                .body("User registered successfully");
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		Authentication principal=SecurityContextHolder.getContext().getAuthentication();
		String username = principal.getName();

		// Example: Get role from Authentication
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String role = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(", "));

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

		model.addAttribute("username", username);
		model.addAttribute("role", role);
		model.addAttribute("today", today);

		return "dashboard";
	}
}
