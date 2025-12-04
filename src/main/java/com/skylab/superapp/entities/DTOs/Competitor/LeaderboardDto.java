package com.skylab.superapp.entities.DTOs.Competitor;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardDto {
    private UserDto user;
    private Double totalScore;
    private Long eventCount;
    private Integer rank;
}