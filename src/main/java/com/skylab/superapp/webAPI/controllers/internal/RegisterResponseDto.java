package com.skylab.superapp.webAPI.controllers.internal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDto {
    private String ldapSkyNumber;

    public RegisterResponseDto(String ldapSkyNumber) {
        this.ldapSkyNumber = ldapSkyNumber;
    }
}
