package com.skylab.superapp.entities.DTOs.Competitor;

import com.skylab.superapp.core.constants.CompetitorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCompetitorRequest {

    @NotNull(message = CompetitorMessages.USER_ID_NOT_NULL)
    private UUID userId;

    @NotNull(message = CompetitorMessages.EVENT_ID_NOT_NULL)
    private UUID eventId;

    private double points;

    private boolean isWinner;
}
