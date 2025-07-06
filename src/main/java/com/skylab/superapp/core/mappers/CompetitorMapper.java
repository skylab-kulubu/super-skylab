package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompetitorMapper {

    public GetCompetitorDto toDto(Competitor competitor) {
        return GetCompetitorDto.builder()
                .id(competitor.getId())
                .firstName(competitor.getUser().getFirstName())
                .lastName(competitor.getUser().getLastName())
                .username(competitor.getUser().getUsername())
                .points(competitor.getPoints())
                .isWinner(competitor.isWinner())
                .eventName(competitor.getEvent().getName())
                .eventType(competitor.getEvent().getType().getName())
                .build();
    }

    public List<GetCompetitorDto> toDtoList(List<Competitor> competitors) {
        return competitors.stream()
                .map(this::toDto)
                .toList();
    }
}