package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Auth.AuthRequest;

public interface AuthService {

    DataResult<String> login(AuthRequest authRequest);


}
