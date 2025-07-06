package com.skylab.superapp.entities.DTOs.Competitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetCompetitorDto {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private double points;
    private boolean isWinner;
    private String eventName;
    private String eventType;
}