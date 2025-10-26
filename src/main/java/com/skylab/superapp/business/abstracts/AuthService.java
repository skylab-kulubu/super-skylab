package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;

public interface AuthService {
    UserDto register(CreateUserRequest createUserRequest);

}
