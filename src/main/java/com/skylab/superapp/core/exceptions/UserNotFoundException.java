package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class UserNotFoundException extends SuperSkyLabException {

    public UserNotFoundException() {
        super(UserMessages.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.USER_NOT_FOUND);
    }

}