package com.skylab.superapp.core.utilities.keycloak.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserKeycloakRequest {

    @NotBlank(message = "Username cannot be blank!")
    private String username;

    @Email(message = "Email must be a valid email address!")
    @NotBlank(message = "Email cannot be blank!")
    private String email;

    @NotBlank(message = "Password cannot be blank!")
    private String password;

    @NotBlank(message = "First name cannot be blank!")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank!")
    private String lastName;

    private String phoneNumber;

}
