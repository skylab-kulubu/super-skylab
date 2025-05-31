package com.skylab.superapp.entities.DTOs.Season;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.Season;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetSeasonDto {
    private int id;
    private String name;
    private String startDate;
    private String endDate;
    private String tenant;

    @JsonProperty("isActive")
    private boolean isActive;

   private List<GetEventDto> events;




    public GetSeasonDto(Season season) {
        this.id = season.getId();
        this.name = season.getName();
        this.startDate = season.getStartDate().toString();
        this.endDate = season.getEndDate().toString();
        this.tenant = season.getType().getName();
        this.isActive = season.isActive();
        this.events = GetEventDto.buildListGetEventDto(season.getEvents());

    }

    public static List<GetSeasonDto> buildListGetSeasonDto(List<Season> seasons) {
        return seasons.stream()
                .map(GetSeasonDto::new)
                .toList();
    }
}
