package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.ErrorDataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.security.JwtService;
import com.skylab.superapp.entities.DTOs.Auth.AuthRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public DataResult<String> generateToken(@RequestBody AuthRequest authRequest) {
        var result = authService.login(authRequest);

        if (result.isSuccess()) {
            return new SuccessDataResult<>(result.getData(), result.getMessage(), result.getHttpStatus());
        } else {
            return new ErrorDataResult<>(result.getMessage(), result.getHttpStatus());
        }

    }



}
