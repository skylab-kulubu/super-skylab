package com.skylab.superapp.entities.DTOs.Competitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompetitorDto {
    private int userId;
    private int eventId;
}
