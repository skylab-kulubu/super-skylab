package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.security.JwtService;
import com.skylab.superapp.entities.DTOs.Auth.AuthRequest;
import com.skylab.superapp.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthManager implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthManager(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public DataResult<String > login(AuthRequest authRequest) {
        if (authRequest.getUsernameOrEmail() == null){
            throw new UsernameOrEmailCannotBeNullException();
        }

        if (authRequest.getPassword() == null){
            throw new PasswordCannotBeNullException();
        }

        DataResult<User> userResult;

        if (DetermineIsUsernameOrEmail(authRequest.getUsernameOrEmail())){
            userResult = userService.getUserEntityByEmail(authRequest.getUsernameOrEmail());
            if (!userResult.isSuccess()) {
                throw new UserNotFoundByEmailException();
            }
        } else {
            userResult = userService.getUserEntityByUsername(authRequest.getUsernameOrEmail());
            if (!userResult.isSuccess()) {
                throw new UserNotFoundByUsernameException();
            }

        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userResult.getData().getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            userService.setLastLoginWithUsername(userResult.getData().getUsername());
            String token = jwtService.generateToken(userResult.getData().getUsername(), userResult.getData().getAuthorities());
            return new SuccessDataResult<>(token, AuthMessages.TOKEN_GENERATED_SUCCESSFULLY, HttpStatus.CREATED, userResult.getPath());
        }else {
            throw new InvalidUsernameOrPasswordException();
        }

    }

    // 1 is email, 0 is username
    private boolean DetermineIsUsernameOrEmail(String mailOrUsername){
        return mailOrUsername.contains("@");
    }
}
