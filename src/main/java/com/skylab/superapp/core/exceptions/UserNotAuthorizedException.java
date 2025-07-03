package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class UserNotAuthorizedException extends SuperSkyLabException {

    public UserNotAuthorizedException() {
        super(UserMessages.USER_NOT_AUTHORIZED, ErrorCode.USER_NOT_AUTHORIZED);
    }

    public UserNotAuthorizedException(String customMessage) {
        super(customMessage, ErrorCode.USER_NOT_AUTHORIZED);
    }
}
