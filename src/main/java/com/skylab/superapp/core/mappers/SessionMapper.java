package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import com.skylab.superapp.entities.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ImageMapper.class, EventMapper.class})
public interface SessionMapper {
    
    @Mapping(target = "speakerImageUrl", source = "speakerImage")
    @Mapping(target = "event", ignore = true)
    SessionDto toDto(Session session);

    @Mapping(target = "speakerImageUrl", source = "speakerImage")
    @Mapping(target = "event", source = "eventDay.event")
    SessionDto toDtoWithEvent(Session session);
}