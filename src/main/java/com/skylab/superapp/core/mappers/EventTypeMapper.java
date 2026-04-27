package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventTypeMapper {

    EventTypeDto toDto(EventType eventType);

}