package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class PasswordsDoNotMatchException extends SuperSkyLabException {

  public PasswordsDoNotMatchException() {
    super(UserMessages.PASSWORDS_DO_NOT_MATCH, ErrorCode.PASSWORDS_DO_NOT_MATCH);
  }

  public PasswordsDoNotMatchException(String customMessage) {
    super(customMessage, ErrorCode.PASSWORDS_DO_NOT_MATCH);
  }

}
