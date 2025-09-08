package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SeasonDoesNotContainEventException extends SuperSkyLabException {

    public SeasonDoesNotContainEventException() {
        super(SeasonMessages.SEASON_NOT_FOUND_IN_SEASON, ErrorCode.EVENT_NOT_FOUND_IN_SEASON);
    }

    public SeasonDoesNotContainEventException(String customMessage) {
        super(customMessage, ErrorCode.EVENT_NOT_FOUND_IN_SEASON);
    }

}
