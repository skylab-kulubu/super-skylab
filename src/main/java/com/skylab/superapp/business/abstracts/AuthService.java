package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.Auth.AuthLoginRequest;
import com.skylab.superapp.entities.DTOs.Auth.AuthRegisterRequest;

public interface AuthService {

    //String login(AuthLoginRequest authLoginRequest);

    void register(AuthRegisterRequest authRegisterRequest);

}
