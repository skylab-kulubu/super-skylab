package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.Event;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class EventMapper {

    private final EventTypeMapper eventTypeMapper;
    private final ImageMapper imageMapper;
    private final SessionMapper sessionMapper;
    private final SeasonMapper seasonMapper;

    public EventMapper(@Lazy EventTypeMapper eventTypeMapper,
                       @Lazy ImageMapper imageMapper, @Lazy SessionMapper sessionMapper,
                       @Lazy SeasonMapper seasonMapper) {
        this.eventTypeMapper = eventTypeMapper;
        this.imageMapper = imageMapper;
        this.sessionMapper = sessionMapper;
        this.seasonMapper = seasonMapper;
    }

    public EventDto toDto(Event event,
                          boolean includeEventType,
                          boolean includeSession,
                          List<CompetitorDto> competitorDtos,
                          boolean includeImages,
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
                competitorDtos
        );
    }

    public EventDto toDto(Event event) {
        return toDto(event, false, false, null, false, false);
    }

    public List<EventDto> toDtoList(List<Event> events) {
        if (events == null) return List.of();
        return events.stream().map(this::toDto).toList();
    }

}