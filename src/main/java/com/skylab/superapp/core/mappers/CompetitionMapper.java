package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.competition.GetCompetitionDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompetitionMapper {
    private final EventTypeMapper eventTypeMapper;
    private final EventMapper eventMapper;

    public CompetitionMapper(@Lazy EventTypeMapper eventTypeMapper,@Lazy EventMapper eventMapper) {
        this.eventTypeMapper = eventTypeMapper;
        this.eventMapper = eventMapper;
    }

    public GetCompetitionDto toDto(Competition competition) {
        return GetCompetitionDto.builder()
                .id(competition.getId())
                .name(competition.getName())
                .active(competition.isActive())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .eventType(eventTypeMapper.toDto(competition.getEventType()))
                .events(eventMapper.toDetailsDtoList(competition.getEvents()))
                .build();

    }

    public List<GetCompetitionDto> toDtoList(List<Competition> competitions) {
        return competitions.stream()
                .map(this::toDto)
                .toList();
    }

}