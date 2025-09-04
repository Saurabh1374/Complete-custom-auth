package com.kitchome.auth.authentication;

import java.util.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchome.auth.Exception.ValidationException;
import com.kitchome.auth.payload.RegisterUserDTO;
import com.kitchome.auth.payload.ValidationError;
import com.kitchome.auth.payload.ValidationResult;
import com.kitchome.auth.payload.projection.UserCredProjection;
import com.kitchome.auth.util.ErrorCode;
import com.kitchome.auth.util.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kitchome.auth.dao.UserRepositoryDao;
import com.kitchome.auth.entity.User;

@Service
public class UserCredentials implements UserDetailsService {
	
	private final UserRepositoryDao userRepo;
	private final PasswordEncoder encryptionStrategy;
	private final ObjectMapper mapper;
	

	public UserCredentials(UserRepositoryDao userRepo, PasswordEncoder encryptionStrategy, ObjectMapper mapper) {
		super();
		this.userRepo = userRepo;
        this.encryptionStrategy = encryptionStrategy;
        this.mapper = mapper;
    }


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		 Optional<UserCredProjection> userOpt = userRepo.findByEmailIgnoreCase(username);
	        if (userOpt.isEmpty()) {
	            userOpt = userRepo.findByUsernameIgnoreCase(username);
	        }

	        return userOpt.map(CustomUserDetails::new)
	                      .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + username));
	    
	}
	public Boolean registerUser(RegisterUserDTO userdto) {
		// register user
		ValidationResult result=userAlreadyExists(userdto.getEmail());
		if(!result.isvalid()){throw new ValidationException(result.getValidationError(),"Email Already exist");
		}
		userdto.setPassword(encryptionStrategy.encode(userdto.getPassword()));
		User user = mapper.convertValue(userdto, User.class);
		user.setRoles(Role.USER);
		userRepo.save(user);
		return true;
	}
	private ValidationResult userAlreadyExists(String email) {
		if (userRepo.existsByEmail(email)) {
			Map<String, String> errorParams=new HashMap<>();
			errorParams.put("Email", email);
			return new ValidationResult(new ValidationError(errorParams, ErrorCode.USER_ALREADY_AVAILABLE));
		}
		return new ValidationResult();
	}




}
