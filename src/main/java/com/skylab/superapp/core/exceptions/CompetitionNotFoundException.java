package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class CompetitionNotFoundException extends SuperSkyLabException {

    public CompetitionNotFoundException() {
        super(CompetitionMessages.COMPETITION_NOT_FOUND, ErrorCode.COMPETITION_NOT_FOUND);
    }

    public CompetitionNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.COMPETITION_NOT_FOUND);
    }

}
