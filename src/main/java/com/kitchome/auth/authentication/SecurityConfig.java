package com.kitchome.auth.authentication;
/*
 * Author: saurabh sameer
 * Date: 3-03-2025
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
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
	 @Autowired
	    private UserDetailsService userDetailsService; 
	@Bean
	public SecurityFilterChain securityHttpConfig(HttpSecurity http) throws Exception {
		 http
				.authorizeHttpRequests(
						authz -> authz.
						requestMatchers("/api/v1/public","/api/v1/users/register","/","/static/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
						.anyRequest().authenticated())
				.csrf(csrf -> csrf.disable())
				.httpBasic(Customizer.withDefaults())
				.logout(logout -> logout
					    .logoutUrl("/logout")
					    .logoutSuccessUrl("/") // or "/"
					    .invalidateHttpSession(true)
					    .deleteCookies("JSESSIONID")
					);
		 return http.build();
				

				//.formLogin(Customizer.withDefaults())

				

	}
	 @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	        provider.setUserDetailsService(userDetailsService);
	        provider.setPasswordEncoder(pass());
	        return provider;
	    }

//	@Bean
//	public UserDetailsService userDetailsService() {
//
//		return new InMemoryUserDetailsManager(User.builder().username("sam")
//				// {noop} is telling security config not to delegate
//				// any password encode and store it in plain text
//				.password("{noop}common").authorities("ROLE_user").build());
//
//	}
	 @Bean
	    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
	        return http.getSharedObject(AuthenticationManagerBuilder.class)
	                   .authenticationProvider(authenticationProvider())
	                   .build();
	    }

	/*
	 * it is reccomended to use password encoder factory it provides backward
	 * compatiblity
	 */
	@Bean
	public PasswordEncoder pass() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
