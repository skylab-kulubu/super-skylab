package com.skylab.superapp.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_BIZBIZE_ADMIN("BIZBIZE_ADMIN"),
    ROLE_AGC_ADMIN("AGC_ADMIN"),
    ROLE_GECEKODU_ADMIN("GECEKODU_ADMIN");


    private String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
