package com.skylab.superapp.entities.DTOs.competition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CompetitionDto {

    private UUID id;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

    //optional thx for idea ardacelep - yusssss
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EventDto> events;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventTypeDto eventType;


    public CompetitionDto(UUID id, String name, LocalDateTime startDate, LocalDateTime endDate, boolean active,
                          List<EventDto> events, EventTypeDto eventType) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.events = events;
        this.eventType = eventType;
    }
}
