package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class OldAndNewPasswordCannotBeSameException extends SuperSkyLabException {

    public OldAndNewPasswordCannotBeSameException() {
        super(UserMessages.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD, ErrorCode.OLD_AND_NEW_PASSWORD_SAME);
    }

    public OldAndNewPasswordCannotBeSameException(String customMessage) {
        super(customMessage, ErrorCode.OLD_AND_NEW_PASSWORD_SAME);
    }

}
