package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AuthService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.EmailCannotBeNullException;
import com.skylab.superapp.core.exceptions.PasswordCannotBeNullException;
import com.skylab.superapp.core.exceptions.UsernameCannotBeNullException;
import com.skylab.superapp.core.security.keycloak.KeycloakService;
import com.skylab.superapp.entities.DTOs.Auth.AuthRegisterRequest;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AuthManager implements AuthService {

    private final UserService userService;

    private final KeycloakService keycloakService;

    public AuthManager(@Lazy UserService userService, KeycloakService keycloakService) {
        this.userService = userService;
        this.keycloakService = keycloakService;
    }

    /*
    @Override
    public String login(AuthLoginRequest authLoginRequest) {
        if (authLoginRequest.getUsernameOrEmail() == null){
            throw new UsernameOrEmailCannotBeNullException();
        }

        if (authLoginRequest.getPassword() == null){
            throw new PasswordCannotBeNullException();
        }

        User user;

        if (DetermineIsUsernameOrEmail(authLoginRequest.getUsernameOrEmail())){
            user = userService.getUserEntityByEmail(authLoginRequest.getUsernameOrEmail());
        } else {
            user = userService.getUserEntityByUsername(authLoginRequest.getUsernameOrEmail());
        }


        if (authentication.isAuthenticated()) {
            userService.setLastLoginWithUsername(user.getUsername());
            return jwtService.generateToken(user.getUsername(), user.getAuthorities());
        } else {
            throw new InvalidUsernameOrPasswordException();
        }

    }

     */

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

        boolean created = keycloakService.createUser(authRegisterRequest);
        if (!created) {
            throw new RuntimeException("User creation failed in Keycloak");
        }


        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(authRegisterRequest.getUsername());
        createUserRequest.setEmail(authRegisterRequest.getEmail());
        //dont encode password here, it will be done in UserService
        createUserRequest.setPassword(authRegisterRequest.getPassword());

        userService.addUser(createUserRequest);

    }

    // 1 is email, 0 is username
    private boolean DetermineIsUsernameOrEmail(String mailOrUsername){
        return mailOrUsername.contains("@");
    }
}
