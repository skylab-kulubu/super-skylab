package com.skylab.superapp.entities.DTOs.CompetitorEventResult;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.entities.CompetitorEventResult;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCompetitorEventResultDto {
    private Long id; // Yarışmacı Event Sonucu ID'si

    private String competitorName; // Yarışmacının adı

    private String eventName; // Event'in adı

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date eventDate; // Event'in tarihi

    private double points; // Yarışmacının bu event'de aldığı puan


    public GetCompetitorEventResultDto(CompetitorEventResult competitorEventResult) {
        this.id = competitorEventResult.getId();
        this.competitorName = competitorEventResult.getCompetitor().getName();
        this.eventName = competitorEventResult.getEvent().getTitle();
        this.eventDate = competitorEventResult.getEvent().getDate();
        this.points = competitorEventResult.getPoints();
    }

    public static List<GetCompetitorEventResultDto> buildListGetCompetitorEventResultDto(List<CompetitorEventResult> competitorEventResults) {
        return competitorEventResults.stream()
                .map(GetCompetitorEventResultDto::new)
                .toList();
    }
}
