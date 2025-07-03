package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class EventTypeNotFoundException extends SuperSkyLabException {

    public EventTypeNotFoundException() {
        super(EventTypeMessages.EVENT_TYPE_NOT_FOUND, ErrorCode.EVENT_TYPE_NOT_FOUND);
    }

    public EventTypeNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.EVENT_TYPE_NOT_FOUND);
    }

}

