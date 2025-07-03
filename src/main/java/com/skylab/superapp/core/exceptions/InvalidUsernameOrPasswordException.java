package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class InvalidUsernameOrPasswordException extends SuperSkyLabException {

    public InvalidUsernameOrPasswordException() {
        super(AuthMessages.INVALID_USERNAME_OR_PASSWORD, ErrorCode.INVALID_USERNAME_OR_PASSWORD);
    }

    public InvalidUsernameOrPasswordException(String customMessage) {
        super(customMessage, ErrorCode.INVALID_USERNAME_OR_PASSWORD);
    }

}
