package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.ImageMessages;
import com.skylab.superapp.core.results.ErrorCode;

public class ImageNotFoundException extends SuperSkyLabException {

    public ImageNotFoundException() {
        super(ImageMessages.IMAGE_CANNOT_BE_FOUND, ErrorCode.IMAGE_NOT_FOUND);
    }

    public ImageNotFoundException(String customMessage) {
        super(customMessage, ErrorCode.IMAGE_NOT_FOUND);
    }

}
