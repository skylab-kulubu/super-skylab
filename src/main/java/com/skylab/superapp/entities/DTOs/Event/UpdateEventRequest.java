package com.skylab.superapp.entities.DTOs.Event;

import com.skylab.superapp.core.constants.EventMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class UpdateEventRequest {

    @NotNull(message = EventMessages.NAME_NOT_NULL)
    private String name;

    private String description;

    @NotNull(message = EventMessages.LOCATION_NOT_NULL)
    private String location;

    private String type;

    private String formUrl;

    private String prizeInfo;

    private LocalDateTime startDate;

    private boolean isRanked;

    @NotNull(message = EventMessages.EVENT_TYPE_ID_NOT_NULL)
    private UUID typeId;

    @NotNull(message = EventMessages.SEASON_ID_NOT_NULL)
    private UUID seasonId;

    private LocalDateTime endDate;

    private String linkedin;

    private boolean active;

    private UUID competitionId;

}
