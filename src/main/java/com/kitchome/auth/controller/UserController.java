package com.kitchome.auth.controller;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.kitchome.auth.authentication.CustomUserDetails;
import com.kitchome.auth.entity.RefreshToken;
import com.kitchome.auth.entity.User;
import com.kitchome.auth.payload.*;
import com.kitchome.auth.payload.projection.UserCredProjection;
import com.kitchome.auth.service.RefreshTokenService;
import com.kitchome.auth.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kitchome.auth.Exception.ValidationException;
import com.kitchome.auth.service.UserService;
import com.kitchome.auth.util.Validation;

@Controller
@RequestMapping("api/v1/users")
public class UserController {
	private final UserService userService;
	private final Validation validation;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final AuthenticationSuccessHandler successHandler;

	public UserController(UserService userService, Validation validation, AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, AuthenticationSuccessHandler successHandler) {
		super();
		this.userService = userService;
		this.validation =validation;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.successHandler = successHandler;
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
	@PostMapping("/login")
	public ResponseEntity<JwtResponseDTO> login(@RequestBody AuthRequestDTO request,
												HttpServletRequest httpRequest, HttpServletResponse response) {

		// 1. Authenticate user
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
		);

		 UserDetails user = (UserDetails) authentication.getPrincipal();

		// 2. Generate JWT token
		String accessToken = jwtUtil.generateToken(user.getUsername());

		// 3. Extract fingerprint & IP
		String fingerprint = getFingerprint(httpRequest);
		String ip = httpRequest.getRemoteAddr();
		String userAgent = httpRequest.getHeader("User-Agent");
	//add header
		// fix credential leak
		// 4. Generate Refresh Token
		RefreshToken refreshToken = refreshTokenService
				.generateAndStoreRefreshToken(user.getUsername(), fingerprint, ip, userAgent);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(15 * 24 * 60 * 60);
		response.addCookie(refreshCookie);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(new JwtResponseDTO(accessToken, refreshToken.getToken()));
	}
	/*
	* Semantics of HTTP

GET is meant to be safe and idempotent (shouldn’t change server state).

Refreshing a token does change server state (old refresh invalidated, new one issued).

So using GET breaks REST principles.

Caching Issues

Browsers, proxies, CDNs may cache GET responses.

You do not want access tokens cached or accidentally replayed.

Security

GET params can leak in logs, browser history, referrers if you ever put tokens in the query string (bad idea).

Even though here you’re using cookies/localStorage, attackers may exploit the assumption that GET = cacheable.*/
	@PostMapping("/refresh")
	public ResponseEntity<JwtResponseDTO> refreshToken(HttpServletRequest httpRequest,HttpServletResponse response) {
		String rawToken = Arrays.stream(Optional.ofNullable(httpRequest.getCookies()).orElse(new Cookie[0]))
				.filter(c -> "refreshToken".equals(c.getName()))
				.map(Cookie::getValue)
				.findFirst()
				.orElseThrow(() -> new SecurityException("Missing refresh token cookie"));
		String fingerprint = getFingerprint(httpRequest);
		String ip = httpRequest.getRemoteAddr();

		RefreshToken token = refreshTokenService.validate(rawToken);

		// Optional: verify fingerprint/ip/userAgent matches
		if (!token.getFingerprint().equals(fingerprint) || !token.getIp().equals(ip)) {
			throw new SecurityException("Device mismatch or suspicious activity");
		}

		refreshTokenService.invalidate(token); // rotation

		User user = token.getUser();
		String newAccessToken = jwtUtil.generateToken(user.getUsername());
		RefreshToken newRefresh = refreshTokenService
				.generateAndStoreRefreshToken(user.getUsername(), fingerprint, ip, httpRequest.getHeader("User-Agent"));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
		Cookie refreshCookie = new Cookie("refreshToken", newRefresh.getToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(15 * 24 * 60 * 60);
		response.addCookie(refreshCookie);

		return ResponseEntity.ok(new JwtResponseDTO(newAccessToken, newRefresh.getToken()));
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
		CustomUserDetails pass=(CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

		model.addAttribute("username", username);
		model.addAttribute("role", role);
		model.addAttribute("pass", pass.getPassword());
		model.addAttribute("today", today);

		return "dashboard";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login"; // resolves to login.html or login.jsp based on your view setup
	}
	private String getFingerprint(HttpServletRequest request) {
		// Prefer header or cookie
		String fingerprint = request.getHeader("X-Device-Fingerprint");
		if (fingerprint == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie c : cookies) {
					if ("fingerprint".equals(c.getName())) {
						return c.getValue();
					}
				}
			}
		}
		return "unknown";
	}
}
