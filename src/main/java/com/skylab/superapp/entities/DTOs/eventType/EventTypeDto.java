package com.skylab.superapp.entities.DTOs.eventType;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EventTypeDto {

    private UUID id;

    private String name;

    private boolean competitive;


    public EventTypeDto(UUID id, String name, boolean competitive) {
        this.id = id;
        this.name = name;
        this.competitive = competitive;
    }
}
