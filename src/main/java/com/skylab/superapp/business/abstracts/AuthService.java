package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.webAPI.controllers.internal.RegisterRequestDto;
import com.skylab.superapp.webAPI.controllers.internal.RegisterResponseDto;

public interface AuthService {
    UserDto register(CreateUserRequest createUserRequest);

    RegisterResponseDto registerOauth2(RegisterRequestDto registerRequestDto);

}
