package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventMapper;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.Season;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventManager implements EventService {

    private final EventDao eventDao;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;
    private final Logger logger = LoggerFactory.getLogger(EventManager.class);
    private final EventMapper eventMapper;
    private final UserService userService;
    private final SeasonService seasonService;
    private final CompetitorService competitorService;

    private static final Set<String> PRIVILEGED_ROLES = Set.of("ADMIN", "YK", "DK");

    public EventManager(EventDao eventDao, @Lazy CompetitorService competitorService, @Lazy ImageService imageService,
                        @Lazy EventTypeService eventTypeService, EventMapper eventMapper, UserService userService, SeasonService seasonService,
                        @Lazy CompetitorService competitorService1) {
        this.eventDao = eventDao;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
        this.eventMapper = eventMapper;
        this.userService = userService;
        this.seasonService = seasonService;
        this.competitorService = competitorService;
    }

    @Override
    @Transactional
    public EventDto addEvent(CreateEventRequest createEventRequest, MultipartFile coverImageFile) {
        logger.info("Adding new event: {}", createEventRequest.getName());

        EventType eventType = eventTypeService.getEventTypeEntityById(createEventRequest.getEventTypeId());

        checkUserAuthorization(eventType);

        Season season = null;
        if(createEventRequest.getSeasonId() != null) {
            season = seasonService.getSeasonEntityById(createEventRequest.getSeasonId());
        }

        Image savedCoverImage = null;
        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            savedCoverImage = imageService.uploadImage(coverImageFile);
        }


        Event event = Event.builder()
                .name(createEventRequest.getName())
                .description(createEventRequest.getDescription())
                .type(eventType)
                .formUrl(createEventRequest.getFormUrl())
                .startDate(createEventRequest.getStartDate())
                .endDate(createEventRequest.getEndDate())
                .active(createEventRequest.isActive())
                .linkedin(createEventRequest.getLinkedin())
                .location(createEventRequest.getLocation())
                .coverImage(savedCoverImage)
                .season(season)
                .isRanked(createEventRequest.isRanked())
                .prizeInfo(createEventRequest.getPrizeInfo())
                .build();

        logger.info("Event created: {}", event.getName());
        return eventMapper.toDto(eventDao.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(UUID id) {
        logger.info("Deleting event with id: {}", id);
        Event event = getEventEntityById(id);

        checkUserAuthorization(event.getType());

        eventDao.delete(event);

        logger.info("Event deleted with id: {}", id);
    }

    @Override
    @Transactional
    public EventDto updateEvent(UUID id, UpdateEventRequest updateEventRequest) {
        logger.info("Updating event id: {}", id);
        var event = getEventEntityById(id);

        checkUserAuthorization(event.getType());

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
            EventType newType = eventTypeService.getEventTypeEntityById(updateEventRequest.getTypeId());
            event.setType(newType);
        }

        if (updateEventRequest.getSeasonId() != null) {
            Season newSeason = seasonService.getSeasonEntityById(updateEventRequest.getSeasonId());
            event.setSeason(newSeason);
        }

        return eventMapper.toDto(eventDao.save(event));
    }


    @Override
    public List<EventDto> getAllEventsByEventType(EventType eventType, boolean includeEventType, boolean includeSession,
                                                  boolean includeCompetitors, boolean includeImages,
                                                  boolean includeSeason) {
        var eventTypeResult = eventTypeService.getEventTypeEntityById(eventType.getId());

        var list = eventDao.findAllByType(eventTypeResult);
        return convertToDtoList(list, includeEventType, includeSession, includeCompetitors, includeImages, includeSeason);
    }

    @Override
    public EventDto getEventById(UUID id, boolean includeEventType, boolean includeSession,
                                 boolean includeCompetitors, boolean includeImages,
                                 boolean includeSeason) {

        return convertToDto(getEventEntityById(id), includeEventType, includeSession, includeCompetitors, includeImages, includeSeason);
    }


    @Override
    @Transactional
    public void addImagesToEvent(UUID eventId, List<UUID> imageIds) {
        var event = getEventEntityById(eventId);

        checkUserAuthorization(event.getType());


        var images = imageService.getImagesByIds(imageIds);


        for (Image image : images) {
            event.getImages().add(image);
        }
        eventDao.save(event);

    }

    @Override
    @Transactional
    public void removeImagesFromEvent(UUID eventId, List<UUID> imageIds) {
        Event event = getEventEntityById(eventId);

        checkUserAuthorization(event.getType());

        List<Image> images = imageService.getImagesByIds(imageIds);

        for (Image image : images) {
            if (!event.getImages().contains(image)) {
                throw new ResourceNotFoundException(EventMessages.IMAGE_NOT_FOUND_IN_EVENT);
            }
            event.getImages().remove(image);
        }

        eventDao.save(event);
    }

    @Override
    public List<EventDto> getAllFutureEventsByEventType(String eventType, boolean includeEventType, boolean includeSession,
                                                        boolean includeCompetitors, boolean includeImages,
                                                        boolean includeSeason) {
        var eventTypeResult = eventTypeService.getEventTypeEntityByName(eventType);

        var events = eventDao.findAllByType(eventTypeResult);

        return convertToDtoList(events, includeEventType, includeSession, includeCompetitors, includeImages, includeSeason);
    }

    @Override
    public List<EventDto> getAllEvents(boolean includeEventType, boolean includeSession,
                                         boolean includeCompetitors, boolean includeImages,
                                         boolean includeSeason) {


        var events = eventDao.findAll();


        return convertToDtoList(events, includeEventType, includeSession, includeCompetitors, includeImages, includeSeason);
    }

    @Override
    public List<EventDto> getAllEventsByEventTypeName(String eventTypeName, boolean includeEventType,
                                                      boolean includeSession, boolean includeCompetitors,
                                                      boolean includeImages, boolean includeSeason) {


        var eventType = eventTypeService.getEventTypeEntityByName(eventTypeName);
        var events = eventDao.findAllByType(eventType);

        return convertToDtoList(events, includeEventType, includeSession, includeCompetitors, includeImages, includeSeason);
    }

    @Override
    public List<EventDto> getAllEventByIsActive(boolean isActive, boolean includeEventType, boolean includeSession,
                                                boolean includeCompetitors, boolean includeImages,
                                                boolean includeSeason) {

        var events = eventDao.findAllByActive(isActive);

        return convertToDtoList(events, includeEventType, includeSession, includeCompetitors, includeImages, includeSeason);
    }


    @Override
    public Event getEventEntityById(UUID id) {
        return eventDao.findById(id).orElseThrow(()-> new ResourceNotFoundException(EventMessages.EVENT_NOT_FOUND));
    }

    private void checkUserAuthorization(EventType eventType) {
        boolean authorizedUser = userService.getAuthenticatedUser()
                .getRoles()
                .stream()
                .anyMatch(role ->
                        PRIVILEGED_ROLES.contains(role) ||
                                eventType.getAuthorizedRoles().contains(role)
                );

        if (!authorizedUser) {
            logger.error("User not authorized for event type: {}", eventType.getName());
            throw new ResourceNotFoundException(EventMessages.USER_NOT_AUTHORIZED_FOR_EVENT_TYPE);
        }
    }

    private EventDto convertToDto(Event event, boolean includeEventType, boolean includeSession,
                                  boolean includeCompetitors, boolean includeImages, boolean includeSeason) {
        if (event == null) return null;

        List<CompetitorDto> competitorDtos = null;

        if (includeCompetitors) {
            competitorDtos = competitorService.getCompetitorsByEventId(event.getId(), true, false);
        }

        return eventMapper.toDto(event, includeEventType, includeSession, competitorDtos, includeImages, includeSeason);
    }

    private List<EventDto> convertToDtoList(List<Event> events, boolean includeEventType, boolean includeSession,
                                            boolean includeCompetitors, boolean includeImages, boolean includeSeason) {
        if (events == null || events.isEmpty()) return Collections.emptyList();

        return events.stream()
                .map(event -> convertToDto(event, includeEventType, includeSession, includeCompetitors, includeImages, includeSeason))
                .collect(Collectors.toList());
    }



}