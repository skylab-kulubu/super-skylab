package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class RoleAlreadyExistsException extends SuperSkyLabException {

    public RoleAlreadyExistsException() {
        super(UserMessages.ROLE_ALREADY_EXISTS, ErrorCode.ROLE_ALREADY_EXISTS);
    }

    public RoleAlreadyExistsException(String customMessage) {
        super(customMessage, ErrorCode.ROLE_ALREADY_EXISTS);
    }

}
