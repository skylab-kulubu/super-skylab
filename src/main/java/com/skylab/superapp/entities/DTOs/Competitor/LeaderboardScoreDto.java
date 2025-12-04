package com.skylab.superapp.entities.DTOs.Competitor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardScoreDto {
    private UUID userId;
    private Double totalScore;
    private Long eventCount;
}