package com.skylab.superapp.entities.DTOs.Competitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPointsToUserDto {
    private int userId;
    private int eventId;
    private double points;
    private boolean isWinner;
    private String award;
}