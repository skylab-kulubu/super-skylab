package com.skylab.superapp.entities.DTOs.Auth.request;

import com.skylab.superapp.core.constants.AuthMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterRequestDto {

    @NotNull(message = AuthMessages.FIRST_NAME_NOT_NULL)
    private String firstName;

    @NotNull(message = AuthMessages.LAST_NAME_NOT_NULL)
    private String lastName;

    @NotNull(message = AuthMessages.USERNAME_NOT_NULL)
    private String username;

    @NotNull(message = AuthMessages.EMAIL_NOT_NULL)
    @Email(message = AuthMessages.INVALID_EMAIL_FORMAT)
    private String email;

    @NotNull(message = AuthMessages.PASSWORD_NOT_NULL)
    private String password;

}
