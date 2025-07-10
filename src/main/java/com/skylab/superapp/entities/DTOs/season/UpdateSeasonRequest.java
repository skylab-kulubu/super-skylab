package com.skylab.superapp.entities.DTOs.season;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateSeasonRequest {


    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

}
