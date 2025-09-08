package com.skylab.superapp.entities.DTOs.competition;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateCompetitionRequest {

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;
}
