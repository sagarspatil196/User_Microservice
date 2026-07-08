package com.lcwd.user.service.services;

import java.util.List;

import com.lcwd.user.service.entities.User;

public interface UserService {

	// User Operation

	// Create
	User saveUser(User user);

	// Get all user
	List<User> getAllUser();

	// get single user

	User getUser(String userId);
	


}
