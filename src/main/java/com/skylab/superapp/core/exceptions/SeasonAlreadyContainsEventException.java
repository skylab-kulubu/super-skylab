package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SeasonAlreadyContainsEventException extends SuperSkyLabException {

    public SeasonAlreadyContainsEventException() {
        super(SeasonMessages.SEASONS_ALREADY_CONTAINS_EVENT, ErrorCode.SEASON_ALREADY_CONTAINS_EVENT);
    }

    public SeasonAlreadyContainsEventException(String customMessage) {
        super(customMessage, ErrorCode.SEASON_ALREADY_CONTAINS_EVENT);
    }

}