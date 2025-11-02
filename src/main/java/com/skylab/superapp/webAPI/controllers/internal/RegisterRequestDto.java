package com.skylab.superapp.webAPI.controllers.internal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
