package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class PasswordCannotBeNullException extends SuperSkyLabException {

    public PasswordCannotBeNullException() {
        super(AuthMessages.PASSWORD_CANNOT_BE_NULL, ErrorCode.PASSWORD_NULL);
    }

    public PasswordCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.PASSWORD_NULL);
    }

}