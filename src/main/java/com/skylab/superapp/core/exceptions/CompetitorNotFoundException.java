package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class CompetitorNotFoundException extends SuperSkyLabException {

    public CompetitorNotFoundException() {
        super(CompetitorMessages.COMPETITOR_NOT_FOUND, ErrorCode.COMPETITOR_NOT_FOUND);
    }

    public CompetitorNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.COMPETITOR_NOT_FOUND);
    }

}