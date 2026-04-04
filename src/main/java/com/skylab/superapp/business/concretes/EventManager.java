package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventMapper;
import com.skylab.superapp.core.utilities.security.EventSecurityUtils;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.Season;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventManager implements EventService {

    private final EventDao eventDao;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;
    private final EventMapper eventMapper;
    private final SeasonService seasonService;
    private final EventSecurityUtils eventSecurityUtils;

    private static final Logger logger = LoggerFactory.getLogger(EventManager.class);

    @Override
    @Transactional
    public EventDto addEvent(CreateEventRequest createEventRequest, MultipartFile coverImageFile) {
        logger.info("Attempting to add new event: {}", createEventRequest.getName());
        EventType eventType = eventTypeService.getEventTypeEntityById(createEventRequest.getEventTypeId());

        eventSecurityUtils.checkAuthorization(eventType);

        Season season = createEventRequest.getSeasonId() != null
                ? seasonService.getSeasonEntityById(createEventRequest.getSeasonId())
                : null;

        Image savedCoverImage = (coverImageFile != null && !coverImageFile.isEmpty())
                ? imageService.uploadImage(coverImageFile)
                : null;

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

        logger.info("Event successfully persisted with id: {}", event.getId());
        return eventMapper.toDto(eventDao.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(UUID id) {
        logger.info("Attempting to delete event with id: {}", id);
        Event event = getEventEntityById(id);

        eventSecurityUtils.checkAuthorization(event.getType());

        eventDao.delete(event);
        logger.info("Event successfully deleted with id: {}", id);
    }

    @Override
    @Transactional
    public EventDto updateEvent(UUID id, UpdateEventRequest updateEventRequest) {
        logger.info("Attempting to update event id: {}", id);
        Event event = getEventEntityById(id);

        eventSecurityUtils.checkAuthorization(event.getType());

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

        logger.info("Event updated successfully: {}", id);
        return eventMapper.toDto(eventDao.save(event));
    }

    @Override
    public List<EventDto> getAllEventsByEventType(EventType eventType) {
        return convertToDtoList(eventDao.findAllByType(eventTypeService.getEventTypeEntityById(eventType.getId())));
    }

    @Override
    public EventDto getEventById(UUID id) {
        return eventMapper.toDto(getEventEntityById(id), true, true, null, true, true);
    }

    @Override
    @Transactional
    public void addImagesToEvent(UUID eventId, List<UUID> imageIds) {
        logger.info("Adding {} images to event id: {}", imageIds.size(), eventId);
        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkAuthorization(event.getType());

        event.getImages().addAll(imageService.getImagesByIds(imageIds));
        eventDao.save(event);
    }

    @Override
    @Transactional
    public void removeImagesFromEvent(UUID eventId, List<UUID> imageIds) {
        logger.info("Removing {} images from event id: {}", imageIds.size(), eventId);
        Event event = getEventEntityById(eventId);

        eventSecurityUtils.checkAuthorization(event.getType());

        List<Image> images = imageService.getImagesByIds(imageIds);
        for (Image image : images) {
            if (!event.getImages().contains(image)) {
                logger.warn("Image {} not found in event {}. Aborting removal.", image.getId(), eventId);
                throw new ResourceNotFoundException(EventMessages.IMAGE_NOT_FOUND_IN_EVENT);
            }
            event.getImages().remove(image);
        }
        eventDao.save(event);

        logger.info("Images removed successfully from event id: {}", eventId);
    }

    @Override
    public List<EventDto> getAllFutureEventsByEventType(String eventType) {
        return convertToDtoList(eventDao.findAllByType(eventTypeService.getEventTypeEntityByName(eventType)));
    }

    @Override
    public List<EventDto> getAllEvents() {
        return convertToDtoList(eventDao.findAll());
    }

    @Override
    public List<EventDto> getAllEventsByEventTypeName(String eventTypeName) {
        return convertToDtoList(eventDao.findAllByType(eventTypeService.getEventTypeEntityByName(eventTypeName)));
    }

    @Override
    public List<EventDto> getAllEventByIsActive(boolean isActive) {
        return convertToDtoList(eventDao.findAllByActive(isActive));
    }

    @Override
    public Event getEventEntityById(UUID id) {
        return eventDao.findById(id).orElseThrow(() -> {
            logger.error("Event not found with id: {}", id);
            return new ResourceNotFoundException(EventMessages.EVENT_NOT_FOUND);
        });
    }

    private List<EventDto> convertToDtoList(List<Event> events) {
        if (events == null || events.isEmpty()) return Collections.emptyList();
        return events.stream()
                .map(event -> eventMapper.toDto(event, true, false, null, true, true))
                .collect(Collectors.toList());
    }
}