package com.skylab.superapp.core.exceptions;

public class SuperSkyLabException extends RuntimeException {
    private final String messageKey;

    protected SuperSkyLabException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
