package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import com.skylab.superapp.entities.EventType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventTypeService {

    EventTypeDto getEventTypeById(UUID eventTypeId);

    EventTypeDto getEventTypeByName(String eventTypeName);

    List<EventTypeDto> getAllEventTypes();

    EventTypeDto addEventType(CreateEventTypeRequest createEventTypeRequest);

    EventTypeDto updateEventType(UUID id, UpdateEventTypeRequest updateEventTypeRequest);

    void deleteEventType(UUID id);

    EventType getEventTypeEntityById(UUID eventTypeId);

    EventType getEventTypeEntityByName(String eventTypeName);

    Set<UserDto> getCoordinatorsByEventTypeName(String eventTypeName);
}