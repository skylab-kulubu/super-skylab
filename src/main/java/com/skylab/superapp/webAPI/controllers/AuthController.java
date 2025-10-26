package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import jakarta.validation.Valid;
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

    @PostMapping("/register")
    public ResponseEntity<SuccessResult> register(@RequestBody @Valid CreateUserRequest createUserRequest){

        authService.register(createUserRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new SuccessResult(AuthMessages.USER_REGISTERED_SUCCESSFULLY,
                                HttpStatus.CREATED)
                );



    }




}
