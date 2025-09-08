package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.constants.ImageMessages;
import com.skylab.superapp.core.results.ErrorCode;


public class ImageCannotBeNullException extends SuperSkyLabException {

    public ImageCannotBeNullException() {
        super(ImageMessages.IMAGE_CANNOT_BE_NULL, ErrorCode.IMAGE_CANNOT_BE_NULL);
    }

    public ImageCannotBeNullException(String customMessage) {
        super(customMessage, ErrorCode.IMAGE_CANNOT_BE_NULL);
    }

}