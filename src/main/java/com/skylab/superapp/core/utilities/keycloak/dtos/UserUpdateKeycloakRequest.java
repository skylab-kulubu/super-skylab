package com.skylab.superapp.core.utilities.keycloak.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateKeycloakRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
