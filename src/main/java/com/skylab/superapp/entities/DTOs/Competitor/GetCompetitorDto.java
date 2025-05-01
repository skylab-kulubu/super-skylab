package com.skylab.superapp.entities.DTOs.Competitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skylab.superapp.entities.Competitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCompetitorDto {
    private String id;

    private String name;

    private String tenant;

    @JsonProperty("isActive")
    private boolean isActive;

    private double totalPoints;

    private int competitionCount;

    public GetCompetitorDto(Competitor competitor) {
        this.id = competitor.getId();
        this.name = competitor.getName();
        this.tenant = competitor.getTenant();
        this.isActive = competitor.isActive();
        this.totalPoints = competitor.getTotalPoints();
        this.competitionCount = competitor.getCompetitionCount();
    }

    public List<GetCompetitorDto> buildListGetCompetitorDto(List<Competitor> competitors) {
        return competitors.stream()
                .map(GetCompetitorDto::new)
                .toList();
    }
}
