package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.Auth.AuthLoginRequest;
import com.skylab.superapp.entities.DTOs.Auth.AuthRegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    /*




    @PostMapping("/login")
    public ResponseEntity<DataResult<String>> generateToken(@RequestBody AuthLoginRequest authLoginRequest,
                                                            HttpServletRequest request) {
        var result = authService.login(authLoginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, AuthMessages.TOKEN_GENERATED_SUCCESSFULLY,
                        HttpStatus.OK, request.getRequestURI()));
    }

     */

    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody AuthRegisterRequest authRequest, HttpServletRequest request) {
        authService.register(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(AuthMessages.USER_REGISTERED_SUCCESSFULLY,
                        HttpStatus.CREATED, request.getRequestURI()));
    }

}
