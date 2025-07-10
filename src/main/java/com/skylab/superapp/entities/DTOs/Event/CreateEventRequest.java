package com.skylab.superapp.entities.DTOs.Event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateEventRequest {

    private String name;

    private String description;

    private String location;

    private UUID eventTypeId;

    private String formUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String linkedin;

    private boolean active;

    private UUID competitionId;

}
