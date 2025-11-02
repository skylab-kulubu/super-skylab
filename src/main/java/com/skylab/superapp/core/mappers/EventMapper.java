package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.Event;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class EventMapper {

    private final EventTypeMapper eventTypeMapper;
    private final CompetitionMapper competitionMapper;
    private final CompetitorMapper competitorMapper;
    private final ImageMapper imageMapper;
    private final SessionMapper sessionMapper;
    private final SeasonMapper seasonMapper;

    public EventMapper(@Lazy EventTypeMapper eventTypeMapper, @Lazy CompetitionMapper competitionMapper,
                       @Lazy ImageMapper imageMapper, @Lazy SessionMapper sessionMapper,
                       @Lazy SeasonMapper seasonMapper, @Lazy CompetitorMapper competitorMapper) {
        this.eventTypeMapper = eventTypeMapper;
        this.competitionMapper = competitionMapper;
        this.imageMapper = imageMapper;
        this.sessionMapper = sessionMapper;
        this.seasonMapper = seasonMapper;
        this.competitorMapper = competitorMapper;
    }

    public EventDto toDto(Event event, boolean includeEventType, boolean includeSession,
                          boolean includeCompetitors, boolean includeImages,
                          boolean includeSeason, boolean includeCompetition) {

        if (event == null) {
            return null;
        }
        return new EventDto(
                event.getId(),
                event.getName(),
                imageMapper.toString(event.getCoverImage()),
                event.getDescription(),
                event.getLocation(),
                includeEventType ? eventTypeMapper.toDto(event.getType()) : null,
                event.getFormUrl(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLinkedin(),
                event.isActive(),
                includeCompetition ? competitionMapper.toDto(event.getCompetition()) : null,
                includeSession ? sessionMapper.toDtoList(event.getSessions()) : null,
                includeImages ? imageMapper.toStringList(event.getImages()) : null,
                includeCompetitors ? competitorMapper.toDtoList(event.getCompetitors()) : null,
                includeSeason ? seasonMapper.toDto(event.getSeason()) : null);
    }

    public EventDto toDto(Event event) {
        return toDto(event, false, false, false, false, false, false);
    }

    public List<EventDto> toDtoList(List<Event> events, boolean includeEventType, boolean includeSession,
                                    boolean includeCompetitors, boolean includeImages,
                                    boolean includeSeason, boolean includeCompetition) {
        return events.stream()
                .map(event -> toDto(event, includeEventType, includeSession,
                        includeCompetitors, includeImages, includeSeason, includeCompetition))
                .toList();
    }

    public List<EventDto> toDtoList(List<Event> events) {
        return events.stream()
                .map(event -> toDto(event, false, false,
                        false, false, false, false))
                .toList();
    }
}