package com.skylab.superapp.entities.DTOs.Event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.competition.CompetitionDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private UUID id;

    private String name;

    private String coverImageUrl;

    private String description;

    private String location;

    private EventTypeDto type;

    private String formUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String linkedin;

    private boolean active;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CompetitionDto competition;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SessionDto> sessions;

    private List<String> imageUrls;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CompetitorDto> competitors;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SeasonDto season;

}
