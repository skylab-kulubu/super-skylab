package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class SeasonNotFoundException extends SuperSkyLabException {

    public SeasonNotFoundException() {
        super(SeasonMessages.SEASON_NOT_FOUND, ErrorCode.SEASON_NOT_FOUND);
    }

    public SeasonNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.SEASON_NOT_FOUND);
    }

}
