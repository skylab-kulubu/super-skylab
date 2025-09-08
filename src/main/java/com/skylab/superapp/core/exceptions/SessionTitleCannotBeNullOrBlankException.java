package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SessionTitleCannotBeNullOrBlankException extends SuperSkyLabException {

    public SessionTitleCannotBeNullOrBlankException() {
        super(SessionMessages.SESSION_TITLE_CANNOT_BE_BLANK, ErrorCode.SESSION_TITLE_BLANK_OR_NULL);
    }

    public SessionTitleCannotBeNullOrBlankException(String customMessage) {
        super(customMessage, ErrorCode.SESSION_TITLE_BLANK_OR_NULL);
    }

}
