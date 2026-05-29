package com.skylab.superapp.entities.DTOs.eventType;

import com.skylab.superapp.core.constants.EventTypeMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateEventTypeRequest {

    @NotBlank(message = EventTypeMessages.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK)
    private String name;

}
