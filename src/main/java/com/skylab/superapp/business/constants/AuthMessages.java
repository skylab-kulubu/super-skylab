package com.skylab.superapp.business.constants;

import org.springframework.http.HttpStatus;

public class AuthMessages {
    public static String emailOrUsernameCannotBeNull = "Email or username cannot be null";
    public static String passwordCannotBeNull = "Password cannot be null";
    public static String userNotFoundWithThisEmail = "User not found with this email";
    public static String userNotFoundWithThisUsername = "User not found with this username";
    public static String tokenGeneratedSuccessfully = "Token generated successfully";
    public static String invalidUsernameOrPassword = "Invalid username or password";
}
