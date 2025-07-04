package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class UserDoesNotHaveRoleException extends SuperSkyLabException {

    public UserDoesNotHaveRoleException() {
        super(UserMessages.USER_DOESNT_HAVE_ROLE, ErrorCode.USER_DOESNT_HAVE_ROLE);
    }

    public UserDoesNotHaveRoleException(String customMessage) {
        super(customMessage, ErrorCode.USER_DOESNT_HAVE_ROLE);
    }

}
