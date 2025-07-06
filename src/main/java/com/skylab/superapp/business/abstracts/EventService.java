package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetBizbizeEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;

import java.util.List;

public interface EventService {

    Event addEvent(CreateEventDto createEventDto);

    void deleteEvent(int id);

    void updateEvent(GetEventDto getEventDto);

    List<Event> getAllEventsByEventType(EventType eventType);

    Event  getEventById(int id);

    void addImagesToEvent(int eventId, List<Integer> imageIds);

    List<Event> getAllFutureEventsByEventType(String eventType);

    List<Event> getAllEvents();

    List<Event> getAllEventByIsActive(boolean isActive);
}

