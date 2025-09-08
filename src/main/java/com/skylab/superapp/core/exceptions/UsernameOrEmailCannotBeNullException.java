package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class UsernameOrEmailCannotBeNullException extends SuperSkyLabException {

    public UsernameOrEmailCannotBeNullException() {
        super(AuthMessages.EMAIL_OR_USERNAME_CANNOT_BE_NULL, ErrorCode.USERNAME_OR_EMAIL_NULL);
    }

    public UsernameOrEmailCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.USERNAME_OR_EMAIL_NULL);
    }
}