package com.skylab.superapp.webAPI.controllers;

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

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UserService userService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    @PostMapping("/login")
    public DataResult<String> generateToken(@RequestBody AuthRequest authRequest) {
        var user = userService.getUserEntityByUsername(authRequest.getUsername());
        if(user.getData() == null){
            return new ErrorDataResult<>("User not found", HttpStatus.NOT_FOUND);
        }


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getData().getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            //set last login date
            userService.setLastLogin(authRequest.getUsername());
            return new SuccessDataResult<String>(jwtService.generateToken(user.getData().getUsername(), user.getData().getAuthorities()), "Token generated successfully", HttpStatus.OK);
        }
        return new ErrorDataResult<>("Invalid username or password", HttpStatus.BAD_REQUEST);
    }



}
