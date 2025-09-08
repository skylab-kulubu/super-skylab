package com.skylab.superapp.entities.DTOs.Event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.DTOs.competition.CompetitionDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EventDto {

    private UUID id;

    private String name;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageDto> images;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CompetitorDto> competitors;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SeasonDto season;

    public EventDto(UUID id, String name, String description, String location, EventTypeDto type, String formUrl,
                    LocalDateTime startDate, LocalDateTime endDate, String linkedin, boolean active,
                    CompetitionDto competition, List<SessionDto> sessions, List<ImageDto> images,
                    List<CompetitorDto> competitors, SeasonDto season) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.type = type;
        this.formUrl = formUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.linkedin = linkedin;
        this.active = active;
        this.competition = competition;
        this.sessions = sessions;
        this.images = images;
        this.competitors = competitors;
        this.season = season;
    }
}
