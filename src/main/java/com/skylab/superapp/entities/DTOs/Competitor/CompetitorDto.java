package com.skylab.superapp.entities.DTOs.Competitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompetitorDto {

    private UUID id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventDto event;

    private double points;

    private boolean isWinner;

    public CompetitorDto(UUID id, UserDto user, EventDto event, double points, boolean isWinner) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.points = points;
        this.isWinner = isWinner;
    }
}
