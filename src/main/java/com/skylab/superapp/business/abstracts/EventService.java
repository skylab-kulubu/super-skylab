package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EventService {

    EventDto addEvent(CreateEventRequest createEventDto, MultipartFile coverImageFile);

    void deleteEvent(UUID id);

    EventDto updateEvent(UUID id, UpdateEventRequest updateEventRequest);

    List<EventDto> getAllEventsByEventType(EventType eventType, boolean includeEventType, boolean includeSession,
                                           boolean includeCompetitors, boolean includeImages, boolean includeSeason);

    EventDto  getEventById(UUID id, boolean includeEventType, boolean includeSession,
                           boolean includeCompetitors, boolean includeImages, boolean includeSeason);

    Event getEventEntityById(UUID id);

    void addImagesToEvent(UUID eventId, List<UUID> imageIds);

    void removeImagesFromEvent(UUID eventId, List<UUID> imageIds);

    List<EventDto> getAllFutureEventsByEventType(String eventType, boolean includeEventType, boolean includeSession,
                                                 boolean includeCompetitors, boolean includeImages,
                                                 boolean includeSeason);

    List<EventDto> getAllEvents(boolean includeEventType, boolean includeSession,
                                  boolean includeCompetitors, boolean includeImages,
                                  boolean includeSeason);

    List<EventDto> getAllEventsByEventTypeName(String eventTypeName, boolean includeEventType, boolean includeSession,
                                boolean includeCompetitors, boolean includeImages,
                                boolean includeSeason);

    List<EventDto> getAllEventByIsActive(boolean isActive, boolean includeEventType, boolean includeSession,
                                         boolean includeCompetitors, boolean includeImages,
                                         boolean includeSeason);
}

