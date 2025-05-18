package com.skylab.superapp.entities.DTOs.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
