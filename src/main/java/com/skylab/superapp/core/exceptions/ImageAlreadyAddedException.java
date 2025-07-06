package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class ImageAlreadyAddedException extends SuperSkyLabException {

    public ImageAlreadyAddedException() {
        super(AnnouncementMessages.IMAGE_ALREADY_ADDED, ErrorCode.IMAGE_ALREADY_ADDED);
    }

    public ImageAlreadyAddedException(String customMessage) {
        super(customMessage, ErrorCode.IMAGE_ALREADY_ADDED);
    }

}