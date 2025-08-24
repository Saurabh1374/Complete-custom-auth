package com.kitchome.auth.authentication;
/*
 * Author: saurabh sameer
 * Date: 3-03-2025
 * */

import com.kitchome.auth.Exception.CustomAccessDeniedHandler;
import com.kitchome.auth.Exception.CustomBasicAuthenticationEntryPoint;
import com.kitchome.auth.events.CustomAuthenticationFailureHandler;
import com.kitchome.auth.events.CustomAuthenticationSuccessHandler;
import com.kitchome.auth.filters.*;
import com.kitchome.auth.util.JwtUtil;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;


/*
 * here we are configuring spring security to
 * secure just the "/private" rest api
 * which is not a good practice at all. we will
 * correct all of this as we move further
 * */

/*
 * here we are implementing
 * cocurrent session control
 * so that a user can have only ne active session
 */

/*form login is enabled
user credentials are obtained through
post req from login page.
 */

/*
* we are going to implement jwt
* and make authentication stateless
* implementation of concurrent session
* will be change as well as logout logic will
* also  be changed as things are not session based now
*/

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	 private final UserDetailsService userDetailsService;
	 private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
	 private final CustomAuthenticationFailureHandler authenticationFailureHandler;
	private final JwtUtil jwtUtil;
	@Bean
	public SecurityFilterChain securityHttpConfig(HttpSecurity http) throws Exception {
		 http
				.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure())
				 .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						authz -> authz
								.requestMatchers("/api/v1/public","/api/v1/users/register","/","/static/**","/error",
										"/api/v1/users/login","/api/v1/users/refresh","/invalidSession","/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
						.anyRequest().authenticated())
				.csrf(csrf -> csrf.disable())
				 .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
				 .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
				 .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
				 .addFilterBefore(new JwtAuthenticationFilter(jwtUtil,userDetailsService), UsernamePasswordAuthenticationFilter.class)
				 .addFilterAfter(new AlreadyLoggedInFilter(authenticationSuccessHandler),JwtAuthenticationFilter.class);
		 return http.build();
				//.formLogin(Customizer.withDefaults())
	}
	/*
	* this bean keeps track
	* of session creation and
	* expiration.
	*
	* */
	@Bean
	public static HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
	@Bean
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
		return event -> {
			HttpServletResponse response = event.getResponse();
			response.sendRedirect("/api/v1/users/login?session=maxed");
		};
	}

	/*
	* keeps track of active session
	* creates a new session if not available
	* in-memory.
	* */
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
/*
* default authentication provider used
* by spring security.
*
* */
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
