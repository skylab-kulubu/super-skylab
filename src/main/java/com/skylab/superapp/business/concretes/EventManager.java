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
        log.info("Attempting to add new event: {}", createEventRequest.getName());
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

        log.info("Event successfully persisted with id: {}", event.getId());
        return eventMapper.eventToEventDto(eventDao.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(UUID id) {
        log.info("Attempting to delete event with id: {}", id);
        Event event = getEventEntityById(id);

        eventSecurityUtils.checkDelete(event.getType().getName());

        eventDao.delete(event);
        log.info("Event successfully deleted with id: {}", id);
    }

    @Override
    @Transactional
    public EventDto updateEvent(UUID id, UpdateEventRequest updateEventRequest) {
        log.info("Attempting to update event id: {}", id);
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

        log.info("Event updated successfully: {}", id);
        return eventMapper.eventToEventDto(eventDao.save(event));
    }

    @Override
    public List<EventDto> getAllEventsByEventType(EventType eventType) {
            var events = eventDao.findAllByType(eventTypeService.getEventTypeEntityById(eventType.getId()));

            log.info("Found {} events for event type: {}", events.size(), eventType.getName());

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
        log.info("Adding {} images to event id: {}", imageIds.size(), eventId);
        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkUpdate(event.getType().getName());

        event.getImages().addAll(imageService.getImagesByIds(imageIds));
        eventDao.save(event);
    }

    @Override
    @Transactional
    public void removeImagesFromEvent(UUID eventId, List<UUID> imageIds) {
        log.info("Removing {} images from event id: {}", imageIds.size(), eventId);
        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkUpdate(event.getType().getName());

        List<Image> images = imageService.getImagesByIds(imageIds);
        for (Image image : images) {
            if (!event.getImages().contains(image)) {
                log.warn("Image {} not found in event {}. Aborting removal.", image.getId(), eventId);
                throw new ResourceNotFoundException(EventMessages.IMAGE_NOT_FOUND_IN_EVENT);
            }
            event.getImages().remove(image);
        }
        eventDao.save(event);

        log.info("Images removed successfully from event id: {}", eventId);
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
        log.info("Assigning event with id: {} to season with id: {}", eventId, seasonId);

        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkUpdate(event.getType().getName());

        Season season = seasonService.getSeasonEntityById(seasonId);

        if (event.getSeason() != null && event.getSeason().getId().equals(season.getId())) {
            throw new BusinessException(EventMessages.EVENT_ALREADY_ASSIGNED_TO_SEASON);
        }

        event.setSeason(season);
        eventDao.save(event);

        log.info("Event with id: {} successfully assigned to season with id: {}", eventId, seasonId);
    }

    @Transactional
    @Override
    public void removeSeasonFromEvent(UUID eventId) {
        log.info("Removing season from event with id: {}", eventId);

        Event event = getEventEntityById(eventId);
        eventSecurityUtils.checkUpdate(event.getType().getName());

        if (event.getSeason() == null) {
            throw new BusinessException(EventMessages.EVENT_NOT_ASSIGNED_TO_SEASON);
        }
        event.setSeason(null);
        eventDao.save(event);

        log.info("Season successfully removed from event with id: {}", eventId);
    }

    @Override
    @Transactional
    public void applyToEvent(UUID eventId) {
        log.info("Applying to event with id: {}", eventId);

        var eventToAttend = getEventEntityById(eventId);
        var authenticatedUser = userService.getAuthenticatedUserEntity();

        if (ticketDao.existsByOwner_IdAndEvent_Id(authenticatedUser.getId(), eventId)) {
            throw new BusinessException("Zaten bu etkinliğe kayıtlısınız!");
        }

        Ticket ticket = Ticket.builder()
                .owner(authenticatedUser)
                .event(eventToAttend)
                .ticketType(TicketType.REGISTERED)
                .sent(false)
                .build();

        ticketDao.save(ticket);

        //skyMailService.sendTicketCreationEmail(authenticatedUser.getEmail(), eventToAttend.getName());
        log.info("Created REGISTERED ticket for user: {}", authenticatedUser.getEmail());
    }

    @Override
    public Event getEventReference(UUID eventId) {

        log.info("Getting event reference for event id: {}", eventId);

        return eventDao.getReferenceById(eventId);
    }

    @Override
    @Transactional
    public void applyToEventAsGuest(UUID eventId, GuestTicketRequestDto request) {
        log.info("Guest application for event id: {} from email: {}", eventId, request.getEmail());

        var eventToAttend = getEventEntityById(eventId);

        if (ticketDao.existsByGuestEmailAndEvent_Id(request.getEmail(), eventId)) {
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
        log.info("Created GUEST ticket for email: {}", ticket.getGuestEmail());
    }

    @Override
    public List<EventDto> getEventsBySeasonId(UUID seasonId) {
        log.info("Getting events for season id: {}", seasonId);

    Season season = seasonService.getSeasonEntityById(seasonId);

    var events = eventDao.findAllBySeason(season);

    log.info("Found {} events for season id: {}", events.size(), seasonId);

    return events.stream()
            .map(eventMapper::eventToEventDto)
            .collect(Collectors.toList());


    }

    @Override
    public Event getEventEntityById(UUID id) {
        return eventDao.findById(id).orElseThrow(() -> {
            log.error("Event not found with id: {}", id);
            return new ResourceNotFoundException(EventMessages.EVENT_NOT_FOUND);
        });
    }


}