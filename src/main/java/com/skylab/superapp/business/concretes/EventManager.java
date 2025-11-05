package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.ImageAlreadyAddedException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventMapper;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EventManager implements EventService {

    private final EventDao eventDao;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;
    private final CompetitionService competitionService;
    private final Logger logger = LoggerFactory.getLogger(EventManager.class);
    private final EventMapper eventMapper;

    public EventManager(EventDao eventDao, @Lazy CompetitorService competitorService, @Lazy ImageService imageService,
                        @Lazy EventTypeService eventTypeService,@Lazy CompetitionService competitionService, EventMapper eventMapper) {
        this.eventDao = eventDao;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
        this.competitionService = competitionService;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDto addEvent(CreateEventRequest createEventRequest, MultipartFile coverImageFile) {
        logger.info("Adding new event: {}", createEventRequest.getName());
        EventType eventType = null;
        if (createEventRequest.getEventTypeId() != null) {
            eventType = eventTypeService.getEventTypeEntityById(createEventRequest.getEventTypeId());
        }

        Competition competition = null;
        if (createEventRequest.getCompetitionId() != null) {
            competition = competitionService.getCompetitionEntityById(createEventRequest.getCompetitionId());
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
                .competition(competition)
                .coverImage(savedCoverImage)
                .build();

        logger.info("Event created: {}", event.getName());
        return eventMapper.toDto(eventDao.save(event));
    }

    @Override
    public void deleteEvent(UUID id) {
        var event = getEventEntityById(id);
        eventDao.delete(event);
    }

    @Override
    public EventDto updateEvent(UUID id, UpdateEventRequest updateEventRequest) {
        var event = getEventEntityById(id);
        logger.info("Updating event id: {}", event.getId());

        event.setName(updateEventRequest.getName() == null
                ? event.getName()
                : updateEventRequest.getName());
        event.setDescription(updateEventRequest.getDescription() == null
                ? event.getDescription()
                : updateEventRequest.getDescription());
        event.setType(updateEventRequest.getType() == null
                ? event.getType()
                : eventTypeService.getEventTypeEntityByName(updateEventRequest.getType()));
        event.setFormUrl(updateEventRequest.getFormUrl() == null
                ? event.getFormUrl()
                : updateEventRequest.getFormUrl());
        event.setStartDate(updateEventRequest.getStartDate() == null
                ? event.getStartDate()
                : updateEventRequest.getStartDate());
        event.setEndDate(updateEventRequest.getEndDate() == null
                ? event.getEndDate()
                : updateEventRequest.getEndDate());
        event.setLinkedin(updateEventRequest.getLinkedin() == null
                ? event.getLinkedin()
                : updateEventRequest.getLinkedin());
        event.setActive(updateEventRequest.isActive());
        event.setCompetition(updateEventRequest.getCompetitionId() == null
                ? event.getCompetition()
                : competitionService.getCompetitionEntityById(updateEventRequest.getCompetitionId()));

        logger.info("Event updated id: {}", event.getId());
        return eventMapper.toDto(eventDao.save(event));
    }


    @Override
    public List<EventDto> getAllEventsByEventType(EventType eventType, boolean includeEventType, boolean includeSession,
                                                  boolean includeCompetitors, boolean includeImages,
                                                  boolean includeSeason, boolean includeCompetition) {
        var eventTypeResult = eventTypeService.getEventTypeEntityById(eventType.getId());

        var list = eventDao.findAllByType(eventTypeResult);
        return eventMapper.toDtoList(list, includeEventType, includeSession,
                includeCompetitors, includeImages, includeSeason, includeCompetition);
    }

    @Override
    public EventDto getEventById(UUID id, boolean includeEventType, boolean includeSession,
                                 boolean includeCompetitors, boolean includeImages,
                                 boolean includeSeason, boolean includeCompetition) {

        return eventMapper.toDto(getEventEntityById(id), includeEventType, includeSession,
                includeCompetitors, includeImages, includeSeason, includeCompetition);
    }


    @Override
    @Transactional
    public void addImagesToEvent(UUID eventId, List<UUID> imageIds) {
        var event = getEventEntityById(eventId);
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
                                                        boolean includeSeason, boolean includeCompetition) {
        var eventTypeResult = eventTypeService.getEventTypeEntityByName(eventType);

        var events = eventDao.findAllByType(eventTypeResult);

        return eventMapper.toDtoList(events, includeEventType, includeSession,
                includeCompetitors, includeImages, includeSeason, includeCompetition);
    }

    @Override
    public List<EventDto> getAllEvents(boolean includeEventType, boolean includeSession,
                                         boolean includeCompetitors, boolean includeImages,
                                         boolean includeSeason, boolean includeCompetition) {


        var events = eventDao.findAll();


        return eventMapper.toDtoList(events, includeEventType, includeSession,
                includeCompetitors, includeImages, includeSeason, includeCompetition);
    }

    @Override
    public List<EventDto> getAllEventsByEventTypeName(String eventTypeName, boolean includeEventType,
                                                      boolean includeSession, boolean includeCompetitors,
                                                      boolean includeImages, boolean includeSeason,
                                                      boolean includeCompetition) {


        var eventType = eventTypeService.getEventTypeEntityByName(eventTypeName);
        var events = eventDao.findAllByType(eventType);

        return eventMapper.toDtoList(events, includeEventType, includeSession,
                includeCompetitors, includeImages, includeSeason, includeCompetition);
    }

    @Override
    public List<EventDto> getAllEventByIsActive(boolean isActive, boolean includeEventType, boolean includeSession,
                                                boolean includeCompetitors, boolean includeImages,
                                                boolean includeSeason, boolean includeCompetition) {

        var events = eventDao.findAllByActive(isActive);

        return eventMapper.toDtoList(events, includeEventType, includeSession,
                includeCompetitors, includeImages, includeSeason, includeCompetition);
    }


    @Override
    public Event getEventEntityById(UUID id) {
        return eventDao.findById(id).orElseThrow(()-> new ResourceNotFoundException(EventMessages.EVENT_NOT_FOUND));
    }

}