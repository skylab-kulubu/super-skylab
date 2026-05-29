package com.skylab.superapp.entities.DTOs.eventDay;

import com.skylab.superapp.core.constants.EventDayMessages;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDayRequest {

    @NotNull(message = EventDayMessages.EVENT_ID_NOT_NULL)
    private UUID eventId;

    private String name;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
