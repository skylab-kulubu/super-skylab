package com.skylab.superapp.entities.DTOs.CompetitorEventResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateCompetitorEventResultDto {
    private String competitorId; // Yarışmacının ID'si

    private int eventId; // Event'in ID'si

    private double points; // Yarışmacının bu event'de aldığı puan
}
