package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class EventTypeCannotBeNullOrBlankException extends SuperSkyLabException {

    public EventTypeCannotBeNullOrBlankException() {
        super(EventTypeMessages.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK, ErrorCode.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK);
    }

    public EventTypeCannotBeNullOrBlankException(String customMessage) {
        super(customMessage, ErrorCode.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK);
    }

}