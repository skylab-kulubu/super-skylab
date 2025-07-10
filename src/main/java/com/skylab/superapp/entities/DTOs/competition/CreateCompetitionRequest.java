package com.skylab.superapp.entities.DTOs.competition;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateCompetitionRequest {


    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

    private UUID eventTypeId;

}
