package com.skylab.superapp.entities.DTOs.Competitor;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;



@Getter
@Setter
public class PatchCompetitorRequest {
    private UUID userId;
    private UUID eventId;
    private Double points;
    private Boolean isWinner;
}
