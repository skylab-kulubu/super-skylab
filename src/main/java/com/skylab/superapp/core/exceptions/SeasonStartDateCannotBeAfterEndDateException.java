package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class SeasonStartDateCannotBeAfterEndDateException extends SuperSkyLabException {

    public SeasonStartDateCannotBeAfterEndDateException() {
        super(SeasonMessages.SEASON_START_DATE_CANNOT_BE_AFTER_END_DATE, ErrorCode.SEASON_START_DATE_AFTER_END_DATE);
    }

    public SeasonStartDateCannotBeAfterEndDateException(String customMessage) {
        super(customMessage, ErrorCode.SEASON_START_DATE_AFTER_END_DATE);
    }

}
