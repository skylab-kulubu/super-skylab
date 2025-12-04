package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.LdapUser;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CompetitorMapper {

    private final EventMapper eventMapper;


    public CompetitorMapper(@Lazy EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public CompetitorDto toDto(Competitor competitor, UserDto userDto, boolean includeEvent) {
        if (competitor == null) return null;

        return new CompetitorDto(
                competitor.getId(),
                userDto,
                includeEvent ? eventMapper.toDto(competitor.getEvent()) : null,
                competitor.getScore(),
                competitor.getRank(),
                competitor.isWinner()
        );
    }

    public List<CompetitorDto> toDtoList(List<Competitor> competitors,
                                         Map<UUID, UserDto> userDtoMap,
                                         boolean includeUser,
                                         boolean includeEvent) {
        if (competitors == null || competitors.isEmpty()) return List.of();

        return competitors.stream()
                .map(competitor -> {
                    UserDto userDto = null;
                    if (includeUser && competitor.getUser() != null) {
                        userDto = userDtoMap.get(competitor.getUser().getId());
                    }
                    return toDto(competitor, userDto, includeEvent);
                })
                .toList();
    }
}