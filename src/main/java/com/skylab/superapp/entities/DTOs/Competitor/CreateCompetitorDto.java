package com.skylab.superapp.entities.DTOs.Competitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCompetitorDto {

    private String name;

    private String tenant;

    @JsonProperty("isActive")
    private boolean isActive;

    private double totalPoints;

    private int competitionCount;


}
