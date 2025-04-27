package com.kitchome.auth.util;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kitchome.auth.dao.UserRepositoryDao;
import com.kitchome.auth.payload.ValidationError;
import com.kitchome.auth.payload.ValidationResult;

@Component
public class Validation {
	private final UserRepositoryDao userRepo;
 public Validation(UserRepositoryDao userRepo) {
		super();
		this.userRepo = userRepo;
	}
public ValidationResult userAlreadyExists(String email) {
	 if (userRepo.existsByEmail(email)) {
			Map<String, String> errorParams=new HashMap<>();
			errorParams.put("Email", email);
			return new ValidationResult(new ValidationError(errorParams,ErrorCode.USER_ALREADY_AVAILABLE, "User exists already"));
		}
	 return new ValidationResult();
 }
}
