package com.kitchome.auth.authentication;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.kitchome.auth.payload.projection.RolesProjection;
import com.kitchome.auth.payload.projection.UserCredProjection;
import com.kitchome.auth.util.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kitchome.auth.entity.User;

public class CustomUserDetails implements UserDetails{
	/**
	 * implemented equals and hashcode
	 * for session registry and concurrent
	 * session control.
	 */
	private static final long serialVersionUID = 1L;
	private final String userName;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	/**
	 * ⚠️ Avoid Injecting Entity Classes (like User) Directly into Security Layers
	 *
	 * ❌ Why it's a Bad Practice:
	 *
	 * 1. Leaky Abstraction:
	 *    - Tight coupling between the security layer and persistence (JPA) layer.
	 *    - Changes to the User entity may unintentionally break authentication logic.
	 *
	 * 2. Serialization Issues:
	 *    - Spring Security may serialize CustomUserDetails (e.g., for session storage).
	 *    - Including JPA entities can cause LazyInitializationException, performance degradation,
	 *      or even StackOverflowError due to cyclic relationships.
	 *
	 * 3. Accidental Data Exposure:
	 *    - Reusing CustomUserDetails in controllers can expose sensitive fields like passwords,
	 *      salts, or internal relationships.
	 *
	 * 4. Difficult Testing:
	 *    - CustomUserDetails becomes harder to test in isolation and may require
	 *      a full JPA context or real database instance.
	 *
	 * ✅ Recommended Approach:
	 * - Extract only the required fields (e.g., username, password, roles) into CustomUserDetails.
	 * - Use a lightweight DTO if needed inside the security context.
	 * - Let the User entity stay within the repository/data access layer.
	 */
	public CustomUserDetails(UserCredProjection user) {
		super();
		//this.user = user;
		this.userName=user.getUsername();
		this.password=user.getPassword();
		this.authorities=user.getRoles().stream().map(role->
				new SimpleGrantedAuthority("ROLE_"+role.getRole())).
				collect(Collectors.toSet());
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(getClass()!=obj.getClass())
			return false;
		CustomUserDetails other=(CustomUserDetails) obj;
		if(userName==null) {
			if (other.userName != null)
				return false;
		}
		else if (!userName.equals(other.userName)) {
			return false;
		}
		if(password==null){
			if(other.password!=null){
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime=31;
		int result=1;
		result=prime*result + ((userName == null)?0:userName.hashCode());
		result=prime*result+((password == null)?0:password.hashCode());
		return result;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return this.authorities;//user.getRoles().stream().
				//map(role->new SimpleGrantedAuthority("Role_"+role.name())).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.userName;
	}

}
