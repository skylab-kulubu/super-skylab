package com.skylab.superapp.entities.DTOs.Event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateEventRequest {

    private String name;

    private String description;

    private String location;

    private String type;

    private String formUrl;

    private String prizeInfo;

    private LocalDateTime startDate;

    private boolean isRanked;

    private UUID typeId;

    private UUID seasonId;

    private LocalDateTime endDate;

    private String linkedin;

    private boolean active;

    private UUID competitionId;

}
