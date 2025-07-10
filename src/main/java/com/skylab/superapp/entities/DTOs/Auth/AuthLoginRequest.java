package com.skylab.superapp.entities.DTOs.Auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLoginRequest {

    private String usernameOrEmail;

    private String password;


}
