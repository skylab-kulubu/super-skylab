package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.exceptions.EventNotFoundException;
import com.skylab.superapp.core.exceptions.ImageAlreadyAddedException;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Image;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventManager implements EventService {

    private final EventDao eventDao;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;

    public EventManager(EventDao eventDao, @Lazy CompetitorService competitorService, @Lazy ImageService imageService,@Lazy EventTypeService eventTypeService) {
        this.eventDao = eventDao;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
    }

    @Override
    public Event addEvent(CreateEventDto createEventDto) {
        var eventType = eventTypeService.getEventTypeByName(createEventDto.getType());


        Event event = Event.builder()
                .name(createEventDto.getTitle())
                .date(createEventDto.getDate())
                .active(createEventDto.isActive())
                .description(createEventDto.getDescription())
                .linkedin(createEventDto.getLinkedin())
                .formUrl(createEventDto.getFormUrl())
                .type(eventType)
                .build();

        return eventDao.save(event);
    }

    @Override
    public void deleteEvent(int id) {
        var event = getEventEntity(id);
        eventDao.delete(event);
    }

    @Override
    public void updateEvent(GetEventDto getEventDto) {
        var event = getEventEntity(getEventDto.getId());

        event.setName(getEventDto.getTitle() == null ? event.getName() : getEventDto.getTitle());
        event.setDate(getEventDto.getDate() == null ? event.getDate() : getEventDto.getDate());
        event.setActive(getEventDto.isActive() == event.isActive() ? event.isActive() : getEventDto.isActive());
        event.setDescription(getEventDto.getDescription() == null ? event.getDescription() : getEventDto.getDescription());

        eventDao.save(event);
    }


    @Override
    public List<Event> getAllEventsByEventType(EventType eventType) {
        var eventTypeResult = eventTypeService.getEventTypeById(eventType.getId());

        return eventDao.findAllByType(eventTypeResult);
    }


    @Override
    public Event getEventById(int id) {
        return getEventEntity(id);
    }


    @Override
    public void addImagesToEvent(int eventId, List<Integer> imageIds) {
        var event = getEventEntity(eventId);
        var images = imageService.getImagesByIds(imageIds);


        List<Image> imageList = new ArrayList<>();
        for (var image : images) {
            if (image.getEvent() != null) {
                throw new ImageAlreadyAddedException();
            }
            image.setEvent(event);
            imageList.add(image);
        }
        event.setImages(imageList);

        eventDao.save(event);
    }

    @Override
    public List<Event> getAllFutureEventsByEventType(String eventType) {
        var eventTypeResult = eventTypeService.getEventTypeByName(eventType);

        return eventDao.findAllByType(eventTypeResult);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventDao.findAll();
    }

    @Override
    public List<Event> getAllEventByIsActive(boolean isActive) {
        return eventDao.findAllByActive(isActive);
    }

    private Event getEventEntity(int id) {
        return eventDao.findById(id).orElseThrow(EventNotFoundException::new);
    }

}