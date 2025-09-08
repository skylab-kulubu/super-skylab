package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SessionNotFoundException extends SuperSkyLabException {

    public SessionNotFoundException() {
        super(SessionMessages.SESSION_NOT_FOUND, ErrorCode.SESSION_NOT_FOUND);
    }

    public SessionNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.SESSION_NOT_FOUND);
    }

}

