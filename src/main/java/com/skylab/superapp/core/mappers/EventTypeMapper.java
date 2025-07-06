package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.DTOs.eventType.GetEventTypeDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventTypeMapper {

    public GetEventTypeDto toDto(EventType eventType) {
        return GetEventTypeDto.builder()
                .id(eventType.getId())
                .name(eventType.getName())
                .isCompetitive(eventType.isCompetitive())
                .build();
    }

    public List<GetEventTypeDto> toDtoList(List<EventType> eventTypes) {
        return eventTypes.stream()
                .map(this::toDto)
                .toList();
    }
}