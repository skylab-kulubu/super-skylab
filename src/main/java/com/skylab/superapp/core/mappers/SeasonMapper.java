package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Season;
import com.skylab.superapp.entities.DTOs.Season.GetSeasonDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeasonMapper {

    private final EventMapper eventMapper;

    public SeasonMapper(@Lazy EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public GetSeasonDto toDto(Season season) {
        return GetSeasonDto.builder()
                .id(season.getId())
                .name(season.getName())
                .startDate(season.getStartDate())
                .endDate(season.getEndDate())
                .isActive(season.isActive())
                .events(season.getEvents() != null ? eventMapper.toDtoList(season.getEvents()) : null)
                .build();
    }

    public List<GetSeasonDto> toDtoList(List<Season> seasons) {
        return seasons.stream()
                .map(this::toDto)
                .toList();
    }
}