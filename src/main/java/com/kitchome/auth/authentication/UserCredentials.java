package com.kitchome.auth.authentication;

import java.util.Collection;
import java.util.Optional;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kitchome.auth.dao.UserRepositoryDao;
import com.kitchome.auth.entity.User;

@Service
public class UserCredentials implements UserDetailsService {
	
	private final UserRepositoryDao userRepo;
	

	public UserCredentials(UserRepositoryDao userRepo) {
		super();
		this.userRepo = userRepo;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		 Optional<User> userOpt = userRepo.findByEmailIgnoreCase(username);
	        if (userOpt.isEmpty()) {
	            userOpt = userRepo.findByUsernameIgnoreCase(username);
	        }

	        return userOpt.map(CustomUserDetails::new)
	                      .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + username));
	    
	}

	
	

}
