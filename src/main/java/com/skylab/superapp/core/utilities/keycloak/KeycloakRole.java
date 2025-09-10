package com.skylab.superapp.core.utilities.keycloak;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeycloakRole {
    USER("user"),
    ADMIN("admin");


    private final String roleName;
}
