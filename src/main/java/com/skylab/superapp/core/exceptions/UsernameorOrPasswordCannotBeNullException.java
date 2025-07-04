package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class UsernameorOrPasswordCannotBeNullException extends SuperSkyLabException {

    public UsernameorOrPasswordCannotBeNullException() {
        super(UserMessages.USERNAME_OR_PASSWORD_CANNOT_BE_NULL, ErrorCode.USERNAME_OR_PASSWORD_NULL);
    }

    public UsernameorOrPasswordCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.USERNAME_OR_PASSWORD_NULL);
    }
}
