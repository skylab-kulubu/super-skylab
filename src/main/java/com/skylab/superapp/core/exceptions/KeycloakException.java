package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.results.ErrorCode;

public class KeycloakException extends SuperSkyLabException {
    public KeycloakException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}
