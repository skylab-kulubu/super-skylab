package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventMapper;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.*;
import com.skylab.superapp.core.security.authz.Authorize;
import com.skylab.superapp.core.security.authz.AuthzKey;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.PatchEventRequest;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import com.skylab.superapp.entities.DTOs.ticket.request.GuestTicketRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventManager implements EventService {

    private final EventDao eventDao;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;
    private final EventMapper eventMapper;
    private final SeasonService seasonService;
    private final UserService userService;
    private final MediaService mediaService;
    private final TicketFactory ticketFactory;

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "CREATE")
    public EventDto addEvent(@AuthzKey CreateEventRequest createEventRequest) {
        log.info("Initiating event creation. EventName: {}", createEventRequest.getName());
        EventType eventType = eventTypeService.getEventTypeEntityById(createEventRequest.getEventTypeId());

        Season season = createEventRequest.getSeasonId() != null
                ? seasonService.getSeasonEntityById(createEventRequest.getSeasonId())
                : null;

        Image coverImage = null;
        if (createEventRequest.getCoverImageId() != null) {
            mediaService.attachImageMedia(createEventRequest.getCoverImageId());
            coverImage = imageService.getImageEntityById(createEventRequest.getCoverImageId());
        }

        Event event = Event.builder()
                .name(createEventRequest.getName())
                .description(createEventRequest.getDescription())
                .type(eventType)
                .capacity(createEventRequest.getCapacity())
                .formUrl(createEventRequest.getFormUrl())
                .startDate(createEventRequest.getStartDate())
                .endDate(createEventRequest.getEndDate())
                .active(createEventRequest.isActive())
                .linkedin(createEventRequest.getLinkedin())
                .location(createEventRequest.getLocation())
                .coverImage(coverImage)
                .season(season)
                .ranked(createEventRequest.isRanked())
                .prizeInfo(createEventRequest.getPrizeInfo())
                .build();

        Event savedEvent = eventDao.save(event);
        log.info("Event created successfully. EventId: {}", savedEvent.getId());

        return eventMapper.eventToEventDto(savedEvent);
    }

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "DELETE")
    public void deleteEvent(@AuthzKey UUID id) {
        log.info("Initiating event deletion. EventId: {}", id);
        Event event = getEventEntityById(id);

        if (event.getSoldTickets() != null && !event.getSoldTickets().isEmpty()) {
            log.warn("Event deletion failed: Has tickets. EventId: {}", id);
            throw new BusinessException(EventMessages.EVENT_HAS_TICKETS);
        }

        if (event.getEventDays() != null && !event.getEventDays().isEmpty()) {
            log.warn("Event deletion failed: Has event days. EventId: {}", id);
            throw new BusinessException(EventMessages.EVENT_HAS_EVENT_DAYS);
        }

        if (event.getCertificates() != null && !event.getCertificates().isEmpty()) {
            log.warn("Event deletion failed: Has certificates. EventId: {}", id);
            throw new BusinessException(EventMessages.EVENT_HAS_CERTIFICATES);
        }

        eventDao.delete(event);
        log.info("Event deleted successfully. EventId: {}", id);
    }

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "UPDATE")
    public EventDto updateEvent(@AuthzKey UUID id, UpdateEventRequest updateEventRequest) {
        log.info("Initiating event replace (PUT). EventId: {}", id);
        Event event = getEventEntityById(id);

        event.setName(updateEventRequest.getName());
        event.setDescription(updateEventRequest.getDescription());
        event.setFormUrl(updateEventRequest.getFormUrl());
        event.setStartDate(updateEventRequest.getStartDate());
        event.setEndDate(updateEventRequest.getEndDate());
        event.setLinkedin(updateEventRequest.getLinkedin());
        event.setLocation(updateEventRequest.getLocation());
        event.setPrizeInfo(updateEventRequest.getPrizeInfo());
        event.setRanked(updateEventRequest.isRanked());
        event.setActive(updateEventRequest.isActive());
        event.setType(eventTypeService.getEventTypeEntityById(updateEventRequest.getTypeId()));
        event.setSeason(seasonService.getSeasonEntityById(updateEventRequest.getSeasonId()));

        Event updatedEvent = eventDao.save(event);
        log.info("Event replaced successfully. EventId: {}", updatedEvent.getId());

        return eventMapper.eventToEventDto(updatedEvent);
    }

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "UPDATE")
    public EventDto patchEvent(@AuthzKey UUID id, PatchEventRequest request) {
        log.info("Initiating event patch (PATCH). EventId: {}", id);
        Event event = getEventEntityById(id);

        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getFormUrl() != null) event.setFormUrl(request.getFormUrl());
        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getLinkedin() != null) event.setLinkedin(request.getLinkedin());
        if (request.getLocation() != null) event.setLocation(request.getLocation());
        if (request.getPrizeInfo() != null) event.setPrizeInfo(request.getPrizeInfo());
        if (request.getIsRanked() != null) event.setRanked(request.getIsRanked());
        if (request.getActive() != null) event.setActive(request.getActive());
        if (request.getTypeId() != null) {
            event.setType(eventTypeService.getEventTypeEntityById(request.getTypeId()));
        }
        if (request.getSeasonId() != null) {
            event.setSeason(seasonService.getSeasonEntityById(request.getSeasonId()));
        }

        Event updatedEvent = eventDao.save(event);
        log.info("Event patched successfully. EventId: {}", updatedEvent.getId());

        return eventMapper.eventToEventDto(updatedEvent);
    }

    @Override
    public List<EventDto> getAllEventsByEventType(EventType eventType) {
        var events = eventDao.findAllByType(eventTypeService.getEventTypeEntityById(eventType.getId()));

        log.info("Retrieved events by type. EventType: {}, TotalCount: {}", eventType.getName(), events.size());

        return events.stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventById(UUID id) {
        return eventMapper.eventToEventDto(getEventEntityById(id));
    }

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "UPDATE")
    public void addImagesToEvent(@AuthzKey UUID eventId, List<UUID> imageIds) {
        log.info("Attaching images to event. EventId: {}, ImageCount: {}", eventId, imageIds.size());
        Event event = getEventEntityById(eventId);

        event.getImages().addAll(imageService.getImagesByIds(imageIds));
        eventDao.save(event);

        log.info("Images attached successfully. EventId: {}", eventId);
    }

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "UPDATE")
    public void removeImagesFromEvent(@AuthzKey UUID eventId, List<UUID> imageIds) {
        log.info("Detaching images from event. EventId: {}, ImageCount: {}", eventId, imageIds.size());
        Event event = getEventEntityById(eventId);

        List<Image> images = imageService.getImagesByIds(imageIds);
        for (Image image : images) {
            if (!event.getImages().contains(image)) {
                log.warn("Failed to detach image: Image not associated with event. EventId: {}, ImageId: {}", eventId, image.getId());
                throw new ResourceNotFoundException(EventMessages.IMAGE_NOT_FOUND_IN_EVENT);
            }
            event.getImages().remove(image);
        }
        eventDao.save(event);

        log.info("Images detached successfully. EventId: {}", eventId);
    }

    @Override
    public List<EventDto> getAllFutureEventsByEventType(String eventType) {
        var futureEvents = eventDao.findAllByType(eventTypeService.getEventTypeEntityByName(eventType));
        return futureEvents.stream().map(eventMapper::eventToEventDto).collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getAllEvents() {
        var events = eventDao.findAll();
        return events.stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getAllEventsByEventTypeName(String eventTypeName) {
        return eventDao.findAllByType(eventTypeService.getEventTypeEntityByName(eventTypeName))
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getAllEventByIsActive(boolean isActive) {
        return eventDao.findAllByActive(isActive)
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Authorize(resource = "EVENT", action = "UPDATE")
    public void assignSeasonToEvent(@AuthzKey UUID eventId, UUID seasonId) {
        log.info("Assigning season to event. EventId: {}, SeasonId: {}", eventId, seasonId);

        Event event = getEventEntityById(eventId);

        Season season = seasonService.getSeasonEntityById(seasonId);

        if (event.getSeason() != null && event.getSeason().getId().equals(season.getId())) {
            log.warn("Season assignment failed: Event already assigned to season. EventId: {}, SeasonId: {}", eventId, seasonId);
            throw new BusinessException(EventMessages.EVENT_ALREADY_ASSIGNED_TO_SEASON);
        }

        event.setSeason(season);
        eventDao.save(event);

        log.info("Season assigned to event successfully. EventId: {}, SeasonId: {}", eventId, seasonId);
    }

    @Transactional
    @Override
    @Authorize(resource = "EVENT", action = "UPDATE")
    public void removeSeasonFromEvent(@AuthzKey UUID eventId, UUID seasonId) {
        log.info("Removing season from event. EventId: {}", eventId);

        Event event = getEventEntityById(eventId);

        if (event.getSeason() == null || !event.getSeason().getId().equals(seasonId)) {
            log.warn("Season removal failed: Event not assigned to specified season. EventId: {}, SeasonId: {}", eventId, seasonId);
            throw new BusinessException(EventMessages.EVENT_NOT_ASSIGNED_TO_THIS_SEASON);
        }


        if (event.getSeason() == null) {
            log.warn("Season removal failed: Event is not assigned to any season. EventId: {}", eventId);
            throw new BusinessException(EventMessages.EVENT_NOT_ASSIGNED_TO_SEASON);
        }

        event.setSeason(null);
        eventDao.save(event);

        log.info("Season removed from event successfully. EventId: {}", eventId);
    }

    @Override
    @Transactional
    public void applyToEvent(UUID eventId) {
        log.info("Processing event registration. EventId: {}", eventId);
        var event = getEventEntityById(eventId);
        var user = userService.getAuthenticatedUserEntity();
        ticketFactory.createRegisteredTicket(user, event);
        log.info("Event registration completed. EventId: {}, UserId: {}", eventId, user.getId());
    }

    @Override
    public Event getEventReference(UUID eventId) {
        log.debug("Retrieving event reference. EventId: {}", eventId);
        return eventDao.getReferenceById(eventId);
    }

    @Override
    @Transactional
    public void applyToEventAsGuest(UUID eventId, GuestTicketRequestDto request) {
        log.info("Processing guest registration. EventId: {}, GuestEmail: {}", eventId, request.getEmail());
        var event = getEventEntityById(eventId);
        ticketFactory.createGuestTicket(event, request);
        log.info("Guest registration completed. EventId: {}, GuestEmail: {}", eventId, request.getEmail());
    }

    @Override
    public List<EventDto> getEventsBySeasonId(UUID seasonId) {
        log.debug("Retrieving events by season. SeasonId: {}", seasonId);

        Season season = seasonService.getSeasonEntityById(seasonId);
        var events = eventDao.findAllBySeason(season);

        log.info("Retrieved events by season. SeasonId: {}, TotalCount: {}", seasonId, events.size());

        return events.stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public Event getEventEntityById(UUID id) {
        return eventDao.findById(id).orElseThrow(() -> {
            log.error("Event retrieval failed: Resource not found. EventId: {}", id);
            return new ResourceNotFoundException(EventMessages.EVENT_NOT_FOUND);
        });
    }
}