package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompetitorMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public CompetitorMapper(@Lazy UserMapper userMapper, @Lazy EventMapper eventMapper) {
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    public CompetitorDto toDto(Competitor competitor, boolean includeUser, boolean includeEvent) {
        if (competitor == null) {
            return null;
        }
        return new CompetitorDto(
                competitor.getId(),
                includeUser ? userMapper.toDto(competitor.getUser()) : null,
                includeEvent ? eventMapper.toDto(competitor.getEvent()) : null,
                competitor.getPoints(),
                competitor.isWinner()
        );
    }

    public CompetitorDto toDto(Competitor competitor) {
        return toDto(competitor, false, false);
    }

    public List<CompetitorDto> toDtoList(List<Competitor> competitors, boolean includeUser, boolean includeEvent) {
        return competitors.stream()
                .map(competitor -> toDto(competitor, includeUser, includeEvent))
                .toList();
    }

    public List<CompetitorDto> toDtoList(List<Competitor> competitors) {
        return toDtoList(competitors, false, false);
    }
}