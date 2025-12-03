package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.Event;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class EventMapper {

    private final EventTypeMapper eventTypeMapper;
    private final CompetitorMapper competitorMapper;
    private final ImageMapper imageMapper;
    private final SessionMapper sessionMapper;
    private final SeasonMapper seasonMapper;

    public EventMapper(@Lazy EventTypeMapper eventTypeMapper,
                       @Lazy ImageMapper imageMapper, @Lazy SessionMapper sessionMapper,
                       @Lazy SeasonMapper seasonMapper, @Lazy CompetitorMapper competitorMapper) {
        this.eventTypeMapper = eventTypeMapper;
        this.imageMapper = imageMapper;
        this.sessionMapper = sessionMapper;
        this.seasonMapper = seasonMapper;
        this.competitorMapper = competitorMapper;
    }

    public EventDto toDto(Event event, boolean includeEventType, boolean includeSession,
                          boolean includeCompetitors, boolean includeImages,
                          boolean includeSeason) {

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
                event.isRanked(),
                event.getPrizeInfo(),
                includeSeason ? seasonMapper.toDto(event.getSeason()) : null,
                includeSession ? sessionMapper.toDtoList(event.getSessions()) : null,
                includeImages ? imageMapper.toStringList(event.getImages()) : null,
                includeCompetitors ? competitorMapper.toDtoList(event.getParticipants()) : null
        );

    }

    public EventDto toDto(Event event) {
        return toDto(event, false, false, false, false, false);
    }

    public List<EventDto> toDtoList(List<Event> events) {
        return events.stream()
                .map(this::toDto)
                .toList();
    }

    public List<EventDto> toDtoList(List<Event> events, boolean includeEventType, boolean includeSession,
                                    boolean includeCompetitors, boolean includeImages,
                                    boolean includeSeason) {
        return events.stream()
                .map(event -> toDto(event, includeEventType, includeSession,
                        includeCompetitors, includeImages, includeSeason))
                .toList();
    }

}