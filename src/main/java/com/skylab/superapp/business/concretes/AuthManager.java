package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthManager implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthManager.class);
    private final UserService userService;


    public AuthManager(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDto register(CreateUserRequest createUserRequest) {
        logger.info("Registering user with username: {}, email: {}", createUserRequest.getUsername(), createUserRequest.getEmail());

        return userService.addUser(createUserRequest);

    }
}
