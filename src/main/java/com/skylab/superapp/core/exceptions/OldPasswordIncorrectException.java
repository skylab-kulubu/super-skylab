package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class OldPasswordIncorrectException extends SuperSkyLabException {

    public OldPasswordIncorrectException() {
        super(UserMessages.OLD_PASSWORD_INCORRECT, ErrorCode.OLD_PASSWORD_INCORRECT);
    }

    public OldPasswordIncorrectException(String customMessage) {
        super(customMessage, ErrorCode.OLD_PASSWORD_INCORRECT);
    }

}