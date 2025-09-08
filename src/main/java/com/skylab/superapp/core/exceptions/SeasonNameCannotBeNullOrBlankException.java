package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class SeasonNameCannotBeNullOrBlankException extends SuperSkyLabException {

  public SeasonNameCannotBeNullOrBlankException() {
    super(SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK, ErrorCode.SEASON_NAME_NULL_OR_BLANK);
  }

  public SeasonNameCannotBeNullOrBlankException(String customMessage) {
    super(customMessage, ErrorCode.SEASON_NAME_NULL_OR_BLANK);
  }

}