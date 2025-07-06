package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.GetEventTypeDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;

import java.util.List;

public interface EventTypeService {

    EventType getEventTypeById(int eventTypeId);

    EventType getEventTypeByName(String eventTypeName);

    List<EventType> getAllEventTypes();

    EventType addEventType(CreateEventTypeDto createEventTypeDto);

    void updateEventType(int id, CreateEventTypeDto createEventTypeDto);

    void deleteEventType(int id);



}