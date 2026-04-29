package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventMapper;
import com.skylab.superapp.core.utilities.security.EventSecurityUtils;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.dataAccess.TicketDao;
import com.skylab.superapp.entities.*;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
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
    private final EventSecurityUtils eventSecurityUtils;
    // private final SkyMailService skyMailService;

    private final UserService userService;
    private final TicketDao ticketDao;
    private final MediaService mediaService;

    @Override
    @Transactional
    public EventDto addEvent(CreateEventRequest createEventRequest) {
        log.info("Initiating event creation. EventName: {}", createEventRequest.getName());
        EventType eventType = eventTypeService.getEventTypeEntityById(createEventRequest.getEventTypeId());

        eventSecurityUtils.checkCreate(eventType.getName());

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
    public void deleteEvent(UUID id) {
        log.info("Initiating event deletion. EventId: {}", id);
        Event event = getEventEntityById(id);

        eventSecurityUtils.checkDelete(event.getType().getName());

        eventDao.delete(event);
        log.info("Event deleted successfully. EventId: {}", id);
    }

    @Override
    @Transactional
    public EventDto updateEvent(UUID id, UpdateEventRequest updateEventRequest) {
        log.info("Initiating event update. EventId: {}", id);
        Event event = getEventEntityById(id);

        eventSecurityUtils.checkUpdate(event.getType().getName());

        if (updateEventRequest.getName() != null) event.setName(updateEventRequest.getName());
        if (updateEventRequest.getDescription() != null) event.setDescription(updateEventRequest.getDescription());
        if (updateEventRequest.getFormUrl() != null) event.setFormUrl(updateEventRequest.getFormUrl());
        if (updateEventRequest.getStartDate() != null) event.setStartDate(updateEventRequest.getStartDate());
        if (updateEventRequest.getEndDate() != null) event.setEndDate(updateEventRequest.getEndDate());
        if (updateEventRequest.getLinkedin() != null) event.setLinkedin(updateEventRequest.getLinkedin());
        if (updateEventRequest.getLocation() != null) event.setLocation(updateEventRequest.getLocation());
        if (updateEventRequest.getPrizeInfo() != null) event.setPrizeInfo(updateEventRequest.getPrizeInfo());

        event.setRanked(updateEventRequest.isRanked());
        event.setActive(updateEventRequest.isActive());

        if (updateEventRequest.getTypeId() != null) {
            event.setType(eventTypeService.getEventTypeEntityById(updateEventRequest.getTypeId()));
        }
        if (updateEventRequest.getSeasonId() != null) {
            event.setSeason(seasonService.getSeasonEntityById(updateEventRequest.getSeasonId()));
        }

        Event updatedEvent = eventDao.save(event);
        log.info("Event updated successfully. EventId: {}", updatedEvent.getId());

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
    public void addImagesToEvent(UUID eventId, List<UUID> imageIds) {
        log.info("Attaching images to event. EventId: {}, ImageCount: {}", eventId, imageIds.size());
        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkUpdate(event.getType().getName());

        event.getImages().addAll(imageService.getImagesByIds(imageIds));
        eventDao.save(event);

        log.info("Images attached successfully. EventId: {}", eventId);
    }

    @Override
    @Transactional
    public void removeImagesFromEvent(UUID eventId, List<UUID> imageIds) {
        log.info("Detaching images from event. EventId: {}, ImageCount: {}", eventId, imageIds.size());
        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkUpdate(event.getType().getName());

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
    public void assignSeasonToEvent(UUID eventId, UUID seasonId) {
        log.info("Assigning season to event. EventId: {}, SeasonId: {}", eventId, seasonId);

        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkUpdate(event.getType().getName());

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
    public void removeSeasonFromEvent(UUID eventId) {
        log.info("Removing season from event. EventId: {}", eventId);

        Event event = getEventEntityById(eventId);
        eventSecurityUtils.checkUpdate(event.getType().getName());

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

        var eventToAttend = getEventEntityById(eventId);
        var authenticatedUser = userService.getAuthenticatedUserEntity();

        if (ticketDao.existsByOwner_IdAndEvent_Id(authenticatedUser.getId(), eventId)) {
            log.warn("Event registration failed: User already registered. EventId: {}, UserId: {}", eventId, authenticatedUser.getId());
            throw new BusinessException(EventMessages.USER_ALREADY_REGISTERED);
        }

        Ticket ticket = Ticket.builder()
                .owner(authenticatedUser)
                .event(eventToAttend)
                .ticketType(TicketType.REGISTERED)
                .sent(false)
                .build();

        ticketDao.save(ticket);

        //skyMailService.sendTicketCreationEmail(authenticatedUser.getEmail(), eventToAttend.getName());
        log.info("Event registration completed successfully. EventId: {}, UserEmail: {}", eventId, authenticatedUser.getEmail());
    }

    @Override
    public Event getEventReference(UUID eventId) {
        log.debug("Retrieving event reference. EventId: {}", eventId);
        return eventDao.getReferenceById(eventId);
    }

    @Override
    @Transactional
    public void applyToEventAsGuest(UUID eventId, GuestTicketRequestDto request) {
        log.info("Processing guest event registration. EventId: {}, GuestEmail: {}", eventId, request.getEmail());

        var eventToAttend = getEventEntityById(eventId);

        if (ticketDao.existsByGuestEmailAndEvent_Id(request.getEmail(), eventId)) {
            log.warn("Guest registration failed: Email already registered for event. EventId: {}, GuestEmail: {}", eventId, request.getEmail());
            throw new BusinessException(EventMessages.GUEST_TICKET_ALREADY_EXISTS);
        }

        Ticket ticket = Ticket.builder()
                .event(eventToAttend)
                .ticketType(TicketType.GUEST)
                .guestFirstName(request.getFirstName())
                .guestLastName(request.getLastName())
                .guestEmail(request.getEmail())
                .guestPhoneNumber(request.getPhoneNumber())
                .guestBirthday(request.getBirthDate())
                .guestIsStudent(request.getIsStudent())
                .guestUniversity(request.getUniversity())
                .guestFaculty(request.getFaculty())
                .guestDepartment(request.getDepartment())
                .guestGrade(request.getGrade())
                .guestTcIdentityNumber(request.getTcIdentityNumber())
                .guestCarPlateNumber(request.getCarPlateNumber())
                .customAnswers(request.getCustomAnswers())
                .sent(false)
                .build();

        ticketDao.save(ticket);

        //skyMailService.sendTicketCreationEmail(ticket.getGuestEmail(), eventToAttend.getName());
        log.info("Guest event registration completed successfully. EventId: {}, GuestEmail: {}", eventId, ticket.getGuestEmail());
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