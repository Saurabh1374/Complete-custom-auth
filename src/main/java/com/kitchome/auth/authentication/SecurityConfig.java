package com.kitchome.auth.authentication;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityHttpConfig(HttpSecurity http) throws Exception {
		return http.
				authorizeHttpRequests(authz -> authz.
						requestMatchers("/api/v1/private").authenticated()
						.anyRequest().permitAll())
				.formLogin(Customizer.withDefaults())
				
				.build();
				
				
				}
	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(User.builder().username("sam")
				.password("{noop}common").authorities("ROLE_user")
				.build()
				);	}
	
}
