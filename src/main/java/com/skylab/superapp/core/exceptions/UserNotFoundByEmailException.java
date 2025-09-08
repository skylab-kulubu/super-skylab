package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class UserNotFoundByEmailException extends SuperSkyLabException {

    public UserNotFoundByEmailException() {
        super(AuthMessages.USER_NOT_FOUND_WITH_EMAIL, ErrorCode.USER_NOT_FOUND_BY_EMAIL);
    }

    public UserNotFoundByEmailException(String customMessage) {
        super(customMessage, ErrorCode.USER_NOT_FOUND_BY_EMAIL);
    }
}