package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.exceptions.EventTypeCannotBeNullOrBlankException;
import com.skylab.superapp.core.exceptions.EventTypeNotFoundException;
import com.skylab.superapp.dataAccess.EventTypeDao;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeDto;
import com.skylab.superapp.entities.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventTypeManager implements EventTypeService {

    private final EventTypeDao eventTypeDao;

    @Autowired
    public EventTypeManager(EventTypeDao eventTypeDao) {
        this.eventTypeDao = eventTypeDao;
    }


    @Override
    public EventType getEventTypeById(int eventTypeId) {
        return getEventTypeEntity(eventTypeId);

    }

    @Override
    public EventType getEventTypeByName(String eventTypeName) {
        return eventTypeDao.findByName(eventTypeName)
                .orElseThrow(EventTypeNotFoundException::new);
    }

    @Override
    public List<EventType> getAllEventTypes() {
        return eventTypeDao.findAll();
    }

    @Override
    public EventType addEventType(CreateEventTypeDto createEventTypeDto) {
        if(createEventTypeDto.getName() == null || createEventTypeDto.getName().isEmpty()) {
            throw new EventTypeCannotBeNullOrBlankException();
        }

        var eventType = EventType.builder()
                .name(createEventTypeDto.getName())
                .isCompetitive(createEventTypeDto.isCompetitive())
                .build();

        return eventTypeDao.save(eventType);
    }

    @Override
    public void updateEventType(int id, CreateEventTypeDto createEventTypeDto) {
        var eventType = getEventTypeEntity(id);

        if(createEventTypeDto.getName() == null || createEventTypeDto.getName().isEmpty()) {
            throw new EventTypeCannotBeNullOrBlankException();
        }

        eventType.setName(createEventTypeDto.getName());
        eventType.setCompetitive(createEventTypeDto.isCompetitive());
        eventTypeDao.save(eventType);
    }

    @Override
    public void deleteEventType(int id) {
        var eventType = getEventTypeEntity(id);

        eventTypeDao.delete(eventType);
    }


    private EventType getEventTypeEntity(int id) {
        return eventTypeDao.findById(id).orElseThrow(EventTypeNotFoundException::new);
    }
}