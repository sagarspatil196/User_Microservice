package com.lcwd.user.service.services.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.repositories.UserRepositories;
import com.lcwd.user.service.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepositories userRepositories;

	@Autowired
	private RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public User saveUser(User user) {
		// generate unique userId
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepositories.save(user);
	}

	@Override
	public List<User> getAllUser() {

		return userRepositories.findAll();
	}

	@Override
	public User getUser(String userId) {

		User user = userRepositories.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException("User with given id is not found on server !!  :" + userId));

		Rating[] ratingsofUser = restTemplate.getForObject("http://localhost:8083/ratings/users/" + user.getUserId(),
				Rating[].class);
		List<Rating> ratings = Arrays.stream(ratingsofUser).toList();

		List<Rating> ratingList = ratings.stream().map(rating -> {
		
			ResponseEntity<Hotel> forEntity = restTemplate
			        .getForEntity("http://localhost:8083/ratings/hotels/" + rating.getHotelId(), Hotel.class);
			
			Hotel hotel = forEntity.getBody();
			logger.info("response status code: {} ", forEntity.getStatusCode());

			rating.setHotel(hotel);
			return rating;
		}).collect(Collectors.toList());
		user.setRatings(ratingList);

		return user;
	}

}
