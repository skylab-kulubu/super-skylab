package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class EventIsInAnotherSeasonException extends SuperSkyLabException {

    public EventIsInAnotherSeasonException() {
        super(SeasonMessages.EVENT_IS_IN_ANOTHER_SEASON, ErrorCode.EVENT_IS_IN_ANOTHER_SEASON);
    }

    public EventIsInAnotherSeasonException(String customMessage) {
        super(customMessage, ErrorCode.EVENT_IS_IN_ANOTHER_SEASON);
    }

}
