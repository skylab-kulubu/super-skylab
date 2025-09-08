package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class UserNotFoundByUsernameException extends SuperSkyLabException {

    public UserNotFoundByUsernameException() {
        super(AuthMessages.USER_NOT_FOUND_WITH_USERNAME, ErrorCode.USER_NOT_FOUND_BY_USERNAME);
    }

    public UserNotFoundByUsernameException(String customMessage) {
        super(customMessage, ErrorCode.USER_NOT_FOUND_BY_USERNAME);
    }
}