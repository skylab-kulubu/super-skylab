package com.skylab.superapp.entities.DTOs.User;


import com.skylab.superapp.core.constants.UserMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    @NotNull(message = UserMessages.USERNAME_CANNOT_BE_NULL)
    private String username;

    @NotNull(message = UserMessages.FIRST_NAME_CANNOT_BE_NULL)
    private String firstName;

    @NotNull(message = UserMessages.LAST_NAME_CANNOT_BE_NULL)
    private String lastName;

    @Email(message = UserMessages.INVALID_EMAIL_FORMAT)
    @NotNull(message = UserMessages.EMAIL_CANNOT_BE_NULL)
    private String email;

    @NotNull(message = UserMessages.PASSWORD_CANNOT_BE_NULL)
    private String password;


}
