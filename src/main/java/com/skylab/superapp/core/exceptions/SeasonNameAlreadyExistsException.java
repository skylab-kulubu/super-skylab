package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;



public class SeasonNameAlreadyExistsException extends SuperSkyLabException {

    public SeasonNameAlreadyExistsException() {
        super(SeasonMessages.SEASON_NAME_ALREADY_EXISTS, ErrorCode.SEASON_NAME_ALREADY_EXISTS);
    }

    public SeasonNameAlreadyExistsException(String customMessage) {
        super(customMessage, ErrorCode.SEASON_NAME_ALREADY_EXISTS);
    }

}
