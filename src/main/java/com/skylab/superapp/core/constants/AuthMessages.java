package com.skylab.superapp.core.constants;

public final class AuthMessages {

    public static final String USER_REGISTERED_SUCCESSFULLY = "auth.user.registered.successfully";
    public static final String EMAIL_NOT_NULL = "auth.email.not.null";
    public static final String USERNAME_NOT_NULL = "auth.username.not.null";
    public static final String LAST_NAME_NOT_NULL = "auth.last.name.not.null";
    public static final String FIRST_NAME_NOT_NULL = "auth.first.name.not.null";
    public static final String PASSWORD_NOT_NULL = "auth.password.not.null";
    public static final String INVALID_EMAIL_FORMAT = "auth.invalid.email.format";

    private AuthMessages() {}

    public static final String EMAIL_OR_USERNAME_CANNOT_BE_NULL = "auth.email.or.username.cannot.be.null";
    public static final String PASSWORD_CANNOT_BE_NULL = "auth.password.cannot.be.null";
    public static final String USER_NOT_FOUND_WITH_EMAIL = "auth.user.not.found.with.email";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "auth.user.not.found.with.username";
    public static final String TOKEN_GENERATED_SUCCESSFULLY = "auth.token.generated.successfully";
    public static final String INVALID_USERNAME_OR_PASSWORD = "auth.invalid.username.or.password";
    public static final String EMAIL_OR_PASSWORD_CANNOT_BE_NULL = "auth.email.or.password.cannot.be.null";
    public static final String PASSWORD_TOO_SHORT = "auth.password.too.short";
    public static final String PASSWORDS_DO_NOT_MATCH = "auth.passwords.do.not.match";
}