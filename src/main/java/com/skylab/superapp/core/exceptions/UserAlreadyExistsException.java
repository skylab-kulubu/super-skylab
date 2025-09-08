package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class UserAlreadyExistsException extends SuperSkyLabException {

    public UserAlreadyExistsException() {
        super(UserMessages.USER_ALREAY_EXISTS, ErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(String customMessage) {
        super(customMessage, ErrorCode.USERNAME_OR_EMAIL_NULL);
    }
}