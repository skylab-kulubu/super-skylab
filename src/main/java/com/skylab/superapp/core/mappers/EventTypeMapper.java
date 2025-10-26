package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.EventType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventTypeMapper {

    public EventTypeDto toDto(EventType eventType) {
        if (eventType == null) {
            return null;
        }
        return new EventTypeDto(
                eventType.getId(),
                eventType.getName()
        );
    }

    public List<EventTypeDto> toDtoList(List<EventType> eventTypes) {
        return eventTypes.stream()
                .map(this::toDto)
                .toList();
    }
}