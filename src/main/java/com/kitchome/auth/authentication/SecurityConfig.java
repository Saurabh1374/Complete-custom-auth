package com.kitchome.auth.authentication;
/*
 * Author: saurabh sameer
 * Date: 3-03-2025
 * */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// in the given code we are using form login which is 
// stateful by default it will store a jsession id
// check that in the browser cookies
/*
 * here we are configuring spring security to
 * secure just the "/private" rest api
 * which is not a good practice at all. we will
 * correct all of this as we move further
 * */

/*
 * here we are using basic auth which is very vulnerable
 * as it sends data over the network for each request in
 * username:password format, inspect it and 
 * check the request header in response tab*/

/*this is sent over network for 
 * authorisation, Basic c2FtOmNvbW1vbg==*/

/*we have configured our securityconfig
 * ro secure all api on thia domAIN 
 * EXCEPT PROVIDED URLS*/

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityHttpConfig(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(
						authz -> authz.requestMatchers("/api/v1/public").permitAll().anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				//.formLogin(Customizer.withDefaults())

				.build();

	}

	@Bean
	public UserDetailsService userDetailsService() {

		return new InMemoryUserDetailsManager(User.builder().username("sam")
				// {noop} is telling security config not to delegate
				// any password encode and store it in plain text
				.password("{noop}common").authorities("ROLE_user").build());

	}

	/*
	 * it is reccomended to use password encoder factory it provides backward
	 * compatiblity
	 */
//	@Bean
//	public PasswordEncoder pass() {
//		return new BCryptPasswordEncoder();
//	}

}
