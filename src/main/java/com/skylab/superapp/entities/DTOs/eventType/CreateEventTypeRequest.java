package com.skylab.superapp.entities.DTOs.eventType;

import com.skylab.superapp.core.constants.EventTypeMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateEventTypeRequest {

    @NotNull(message = EventTypeMessages.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK)
    private String name;

    private Set<String> authorizedRoles;

}
