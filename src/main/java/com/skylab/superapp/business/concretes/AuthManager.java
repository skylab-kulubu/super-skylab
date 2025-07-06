package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.security.JwtService;
import com.skylab.superapp.entities.DTOs.Auth.AuthRegisterRequest;
import com.skylab.superapp.entities.DTOs.Auth.AuthRequest;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthManager implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthManager(AuthenticationManager authenticationManager,
                       @Lazy UserService userService,
                       @Lazy JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(AuthRequest authRequest) {
        if (authRequest.getUsernameOrEmail() == null){
            throw new UsernameOrEmailCannotBeNullException();
        }

        if (authRequest.getPassword() == null){
            throw new PasswordCannotBeNullException();
        }

        User user;

        if (DetermineIsUsernameOrEmail(authRequest.getUsernameOrEmail())){
            user = userService.getUserByEmail(authRequest.getUsernameOrEmail());
        } else {
            user = userService.getUserByUsername(authRequest.getUsernameOrEmail());
        }

        // Authentication'da user'ın gerçek username'ini kullan
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            userService.setLastLoginWithUsername(user.getUsername());
            return jwtService.generateToken(user.getUsername(), user.getAuthorities());
        } else {
            throw new InvalidUsernameOrPasswordException();
        }

    }

    @Override
    public void register(AuthRegisterRequest authRegisterRequest) {
        if (authRegisterRequest.getUsername() == null){
            throw new UsernameCannotBeNullException();
        }

        if (authRegisterRequest.getEmail() == null){
            throw new EmailCannotBeNullException();
        }

        if (authRegisterRequest.getPassword() == null){
            throw new PasswordCannotBeNullException();
        }

        /*
        if (userService.existsByUsername(authRegisterRequest.getUsername())){
            throw new UsernameAlreadyExistsException();
        }

        if (userService.existsByEmail(authRegisterRequest.getEmail())){
            throw new EmailAlreadyExistsException();
        }

         */

        CreateUserDto userDto = CreateUserDto.builder()
                .username(authRegisterRequest.getUsername())
                .email(authRegisterRequest.getEmail())
                .password(authRegisterRequest.getPassword())
                .build();

        userService.addUser(userDto);

    }

    // 1 is email, 0 is username
    private boolean DetermineIsUsernameOrEmail(String mailOrUsername){
        return mailOrUsername.contains("@");
    }
}
