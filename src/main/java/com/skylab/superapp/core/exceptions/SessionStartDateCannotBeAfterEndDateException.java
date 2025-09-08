package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SessionStartDateCannotBeAfterEndDateException extends SuperSkyLabException {

    public SessionStartDateCannotBeAfterEndDateException() {
        super(SessionMessages.SESSION_START_DATE_CANNOT_BE_AFTER_END_DATE, ErrorCode.SESSION_START_DATE_AFTER_END_DATE);
    }

    public SessionStartDateCannotBeAfterEndDateException(String customMessage) {
        super(customMessage, ErrorCode.SESSION_START_DATE_AFTER_END_DATE);
    }

}