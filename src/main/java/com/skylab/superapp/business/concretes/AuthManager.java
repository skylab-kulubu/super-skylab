package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.UserProfile;
import com.skylab.superapp.webAPI.controllers.internal.RegisterRequestDto;
import com.skylab.superapp.webAPI.controllers.internal.RegisterResponseDto;
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

    @Override
    public RegisterResponseDto registerOauth2(RegisterRequestDto registerRequestDto) {
        logger.info("Registering OAuth2 user with email: {}", registerRequestDto.getEmail());
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(registerRequestDto.getUsername());
        createUserRequest.setEmail(registerRequestDto.getEmail());
        createUserRequest.setFirstName(registerRequestDto.getFirstName());
        createUserRequest.setLastName(registerRequestDto.getLastName());
        UserDto userDto = userService.addUser(createUserRequest);

        UserProfile userProfile = userService.getUserEntityById(userDto.getId());
        logger.info("OAuth2 user registered with LDAP Sky Number: {}", userProfile.getLdapSkyNumber());
        return new RegisterResponseDto(userProfile.getLdapSkyNumber());

    }
}
