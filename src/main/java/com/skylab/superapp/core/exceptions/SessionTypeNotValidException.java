package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SessionTypeNotValidException extends SuperSkyLabException {

    public SessionTypeNotValidException() {
        super(SessionMessages.SESSION_TYPE_NOT_VALID, ErrorCode.SESSION_TYPE_NOT_VALID);
    }

    public SessionTypeNotValidException(String customMessage) {
        super(customMessage, ErrorCode.SESSION_TYPE_NOT_VALID);
    }

}
