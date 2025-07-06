package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class SessionDatesCannotBeNullException extends SuperSkyLabException {

    public SessionDatesCannotBeNullException() {
        super(SessionMessages.SESSION_DATES_CANNOT_BE_NULL, ErrorCode.SESSION_DATES_CANNOT_BE_NULL);
    }

    public SessionDatesCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.SESSION_DATES_CANNOT_BE_NULL);
    }

}
