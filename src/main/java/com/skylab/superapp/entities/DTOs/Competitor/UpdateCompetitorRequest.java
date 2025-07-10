package com.skylab.superapp.entities.DTOs.Competitor;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCompetitorRequest {

    private UUID userId;

    private UUID eventId;

    private double points;

    private boolean isWinner;


}
