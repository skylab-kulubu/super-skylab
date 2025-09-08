package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class EmailCannotBeNullException extends SuperSkyLabException {

    public EmailCannotBeNullException() {
        super(UserMessages.EMAIL_CANNOT_BE_NULL, ErrorCode.EMAIL_NULL);
    }

    public EmailCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.EMAIL_NULL);
    }
}