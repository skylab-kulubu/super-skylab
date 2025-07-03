package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.constants.AuthMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class AnnouncementNotFoundException extends SuperSkyLabException {

    public AnnouncementNotFoundException() {
        super(AnnouncementMessages.ANNOUNCEMENT_NOT_FOUND, ErrorCode.ANNOUNCEMENT_NOT_FOUND);
    }

    public AnnouncementNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.ANNOUNCEMENT_NOT_FOUND);
    }
}
