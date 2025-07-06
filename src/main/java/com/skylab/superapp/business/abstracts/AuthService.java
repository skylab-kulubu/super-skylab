package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Auth.AuthRegisterRequest;
import com.skylab.superapp.entities.DTOs.Auth.AuthRequest;

public interface AuthService {

    String login(AuthRequest authRequest);

    void register(AuthRegisterRequest authRegisterRequest);


}
