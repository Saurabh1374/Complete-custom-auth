package com.kitchome.auth.controller;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kitchome.auth.Exception.ValidationException;
import com.kitchome.auth.payload.RegisterUserDTO;
import com.kitchome.auth.payload.ValidationResult;
import com.kitchome.auth.service.UserService;
import com.kitchome.auth.util.Validation;

@Controller
@RequestMapping("api/v1/users")
public class UserController {
	private final UserService userService;
	private final Validation validation;

	public UserController(UserService userService,Validation validation) {
		super();
		this.userService = userService;
		this.validation =validation;
	}

	 @GetMapping("/register")
	    public String showRegistrationForm() {
	        return "register"; // This will look for register.html in resources/templates
	    }
	@PostMapping("/register")
	public ResponseEntity<?> createUser(@RequestBody RegisterUserDTO userDto) {
		ValidationResult valid=validation.userAlreadyExists(userDto.getEmail());
		if(!valid.isvalid()) {
			throw new ValidationException(valid.getValidationError(),"Email is already registered ");
		}
		userService.registerUser(userDto);
		 return ResponseEntity
	                .status(HttpStatus.CREATED)
	                .body("User registered successfully");
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) throws BadRequestException {
		Authentication principal=SecurityContextHolder.getContext().getAuthentication();
		if(principal==null || !principal.isAuthenticated() || principal instanceof AnonymousAuthenticationToken) {
			throw new BadRequestException("Bad credentials");
		}
		String username = principal.getName();

		// Example: Get role from Authentication
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String role = principal.getAuthorities() != null? principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(", ")):"N/A";

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

		model.addAttribute("username", username);
		model.addAttribute("role", role);
		model.addAttribute("today", today);

		return "dashboard";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login"; // resolves to login.html or login.jsp based on your view setup
	}
}
