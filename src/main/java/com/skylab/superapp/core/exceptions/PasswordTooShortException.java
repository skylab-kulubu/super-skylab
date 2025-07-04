package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class PasswordTooShortException extends SuperSkyLabException {

  public PasswordTooShortException() {
    super(AuthMessages.PASSWORD_TOO_SHORT, ErrorCode.PASSWORD_TOO_SHORT);
  }

  public PasswordTooShortException(String customMessage) {
    super(customMessage, ErrorCode.PASSWORD_TOO_SHORT);
  }
}
