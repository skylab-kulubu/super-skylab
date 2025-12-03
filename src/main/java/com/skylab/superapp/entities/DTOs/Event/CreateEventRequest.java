package com.skylab.superapp.entities.DTOs.Event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.core.constants.EventMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateEventRequest {

    @NotNull(message = EventMessages.NAME_NOT_NULL)
    private String name;

    private String description;

    @NotNull(message = EventMessages.LOCATION_NOT_NULL)
    private String location;

    @NotNull(message = EventMessages.EVENT_TYPE_ID_NOT_NULL)
    private UUID eventTypeId;

    @NotNull(message = EventMessages.SEASON_ID_NOT_NULL)
    private UUID seasonId;

    private String formUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String linkedin;

    private boolean active;

    private UUID competitionId;

    private boolean isRanked;

    private String prizeInfo;

}
