package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class CompetitorNotParticipatingInEventException extends SuperSkyLabException {

  public CompetitorNotParticipatingInEventException() {
    super(CompetitorMessages.COMPETITOR_NOT_PARTICIPATING_IN_EVENT, ErrorCode.COMPETITOR_NOT_PARTICIPATING_IN_EVENT);
  }

  public CompetitorNotParticipatingInEventException(String customMessage) {
    super(customMessage, ErrorCode.COMPETITOR_NOT_PARTICIPATING_IN_EVENT);
  }
}