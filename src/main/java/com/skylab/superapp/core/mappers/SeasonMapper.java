package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.Season;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeasonMapper {

    private final EventMapper eventMapper;

    public SeasonMapper(@Lazy EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public SeasonDto toDto(Season season, boolean includeEvents) {

        if (season == null) {
            return null;
        }

        return new SeasonDto(
                season.getId(),
                season.getName(),
                season.getStartDate(),
                season.getEndDate(),
                season.isActive(),
                includeEvents ? eventMapper.toDtoList(season.getEvents()) : null
        );
    }

    public SeasonDto toDto(Season season) {
        return toDto(season, false);
    }

    public List<SeasonDto> toDtoList(List<Season> seasons, boolean includeEvents) {
        return seasons.stream()
                .map(season -> toDto(season, includeEvents))
                .toList();
    }

    public List<SeasonDto> toDtoList(List<Season> seasons) {
       return toDtoList(seasons, false);
    }
}