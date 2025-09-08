package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class EventNotFoundException extends SuperSkyLabException {

    public EventNotFoundException() {
        super(EventMessages.EVENT_NOT_FOUND, ErrorCode.EVENT_NOT_FOUND);
    }

    public EventNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.EVENT_NOT_FOUND);
    }
}
