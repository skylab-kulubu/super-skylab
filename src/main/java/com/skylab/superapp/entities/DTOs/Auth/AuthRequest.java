package com.skylab.superapp.entities.DTOs.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthRequest {

    private String username;

    private String password;

}
