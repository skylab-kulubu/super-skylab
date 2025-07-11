package com.skylab.superapp.entities.DTOs.Auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRegisterRequest {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String password;

}
