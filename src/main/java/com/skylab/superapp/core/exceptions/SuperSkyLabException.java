package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.results.ErrorCode;

public class SuperSkyLabException extends RuntimeException {
    private final String messageKey;
    private final ErrorCode errorCode;

    protected SuperSkyLabException(String messageKey, ErrorCode errorCode) {
        super(messageKey);
        this.messageKey = messageKey;
        this.errorCode = errorCode;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
