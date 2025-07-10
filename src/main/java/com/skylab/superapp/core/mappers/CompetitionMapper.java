package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.competition.CompetitionDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompetitionMapper {

    private final EventTypeMapper eventTypeMapper;
    private final EventMapper eventMapper;

    public CompetitionMapper(@Lazy EventTypeMapper eventTypeMapper, @Lazy EventMapper eventMapper) {
        this.eventTypeMapper = eventTypeMapper;
        this.eventMapper = eventMapper;
    }

    public CompetitionDto toDto(Competition competition, boolean includeEvent, boolean includeEventType) {
        if (competition == null) {
            return null;
        }
        return new CompetitionDto(
                competition.getId(),
                competition.getName(),
                competition.getStartDate(),
                competition.getEndDate(),
                competition.isActive(),
                includeEvent ? eventMapper.toDtoList(competition.getEvents()) : null,
                includeEventType ? eventTypeMapper.toDto(competition.getEventType()) : null
        );
    }

    public CompetitionDto toDto(Competition competition) {
        return toDto(competition, false, false);
    }

    public List<CompetitionDto> toDtoList(List<Competition> competitions,
                                          boolean includeEvent, boolean includeEventType) {
        return competitions.stream()
                .map(competition -> toDto(competition, includeEvent, includeEventType))
                .toList();
    }

    public List<CompetitionDto> toDtoList(List<Competition> competitions) {
        return toDtoList(competitions, false, false);
    }


}