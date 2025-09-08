package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;

import java.util.List;
import java.util.UUID;

public interface EventService {

    EventDto addEvent(CreateEventRequest createEventDto);

    void deleteEvent(UUID id);

    EventDto updateEvent(UpdateEventRequest updateEventRequest);

    List<EventDto> getAllEventsByEventType(EventType eventType, boolean includeEventType, boolean includeSession,
                                           boolean includeCompetitors, boolean includeImages, boolean includeSeason,
                                           boolean includeCompetition);

    EventDto  getEventById(UUID id, boolean includeEventType, boolean includeSession,
                           boolean includeCompetitors, boolean includeImages, boolean includeSeason,
                           boolean includeCompetition);

    Event getEventEntityById(UUID id);

    void addImagesToEvent(UUID eventId, List<UUID> imageIds);

    List<EventDto> getAllFutureEventsByEventType(String eventType, boolean includeEventType, boolean includeSession,
                                                 boolean includeCompetitors, boolean includeImages,
                                                 boolean includeSeason, boolean includeCompetition);

    List<EventDto> getAllEvents(boolean includeEventType, boolean includeSession,
                                  boolean includeCompetitors, boolean includeImages,
                                  boolean includeSeason, boolean includeCompetition);

    List<EventDto> getAllEventsByEventTypeName(String eventTypeName, boolean includeEventType, boolean includeSession,
                                boolean includeCompetitors, boolean includeImages,
                                boolean includeSeason, boolean includeCompetition);

    List<EventDto> getAllEventByIsActive(boolean isActive, boolean includeEventType, boolean includeSession,
                                         boolean includeCompetitors, boolean includeImages,
                                         boolean includeSeason, boolean includeCompetition);
}

