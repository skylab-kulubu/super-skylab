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

    List<EventDto> getAllEventsByEventType(EventType eventType);

    EventDto getEventById(UUID id);

    Event getEventEntityById(UUID id);

    void addImagesToEvent(UUID eventId, List<UUID> imageIds);

    void removeImagesFromEvent(UUID eventId, List<UUID> imageIds);

    List<EventDto> getAllFutureEventsByEventType(String eventType);

    List<EventDto> getAllEvents();

    List<EventDto> getAllEventsByEventTypeName(String eventTypeName);

    List<EventDto> getAllEventByIsActive(boolean isActive);

    void assignSeasonToEvent(UUID eventId, UUID seasonId);

    void removeSeasonFromEvent(UUID eventId);

}