package com.kitchome.auth.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kitchome.auth.entity.User;

@Repository
public interface UserRepositoryDao extends JpaRepository<User, Long>{
	Boolean existsByEmail(String email);
	Optional<User> findByEmailIgnoreCase(String email);
	Optional<User> findByUsernameIgnoreCase(String username);

}
