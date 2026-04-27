package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, EventMapper.class})
public interface CompetitorMapper {
    
    @Mapping(target = "event", ignore = true)
    CompetitorDto toDto(Competitor competitor);

    @Mapping(target = "event", source = "event")
    CompetitorDto toDtoWithEvent(Competitor competitor);
}