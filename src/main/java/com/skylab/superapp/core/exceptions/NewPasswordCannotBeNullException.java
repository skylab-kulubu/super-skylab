package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class NewPasswordCannotBeNullException extends SuperSkyLabException {

    public NewPasswordCannotBeNullException() {
        super(UserMessages.NEW_PASSWORD_CANNOT_BE_NULL, ErrorCode.NEW_PASSWORD_NULL);
    }

    public NewPasswordCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.NEW_PASSWORD_NULL);
    }

}