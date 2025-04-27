package com.kitchome.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchome.auth.dao.UserRepositoryDao;

import com.kitchome.auth.entity.User;
import com.kitchome.auth.payload.RegisterUserDTO;
import com.kitchome.auth.util.Role;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepositoryDao userRepo;
	private final ObjectMapper mapper;
	private final PasswordEncoder encryptionStrategy;

	UserServiceImpl(UserRepositoryDao userRepo, ObjectMapper mapper,PasswordEncoder encryptionStrategy) {
		this.userRepo = userRepo;
		this.mapper = mapper;
		this.encryptionStrategy=encryptionStrategy;
	}

	@Override
	public Boolean registerUser(RegisterUserDTO userdto) {
		// register user
		userdto.setPassword(encryptionStrategy.encode(userdto.getPassword()));
		User user = mapper.convertValue(userdto, User.class);
		user.setRoles(Role.USER);
		userRepo.save(user);
		return true;
	}

}
