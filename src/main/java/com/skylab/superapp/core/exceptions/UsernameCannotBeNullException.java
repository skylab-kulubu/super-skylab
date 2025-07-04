package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class UsernameCannotBeNullException extends SuperSkyLabException {

    public UsernameCannotBeNullException() {
        super(UserMessages.USERNAME_CANNOT_BE_NULL, ErrorCode.USERNAME_NULL);
    }

    public UsernameCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.USERNAME_NULL);
    }

}

