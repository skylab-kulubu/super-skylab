package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SessionSpeakerNameCannotBeNullOrBlankException extends SuperSkyLabException {

    public SessionSpeakerNameCannotBeNullOrBlankException() {
        super(SessionMessages.SESSION_SPEAKER_NAME_CANNOT_BE_NULL_OR_BLANK, ErrorCode.SESSION_SPEAKER_NAME_NULL_OR_BLANK);
    }

    public SessionSpeakerNameCannotBeNullOrBlankException(String customMessage) {
        super(customMessage, ErrorCode.SESSION_SPEAKER_NAME_NULL_OR_BLANK);
    }

}

