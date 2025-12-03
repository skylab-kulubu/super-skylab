package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.EventTypeMapper;
import com.skylab.superapp.dataAccess.EventTypeDao;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import com.skylab.superapp.entities.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventTypeManager implements EventTypeService {

    private final EventTypeDao eventTypeDao;
    private final EventTypeMapper eventTypeMapper;

    @Autowired
    public EventTypeManager(EventTypeDao eventTypeDao,
                            EventTypeMapper eventTypeMapper) {
        this.eventTypeDao = eventTypeDao;
        this.eventTypeMapper = eventTypeMapper;
    }


    @Override
    public EventTypeDto getEventTypeById(UUID eventTypeId) {
        return eventTypeMapper.toDto(getEventTypeEntityById(eventTypeId));

    }

    @Override
    public EventTypeDto getEventTypeByName(String eventTypeName) {
        var result =eventTypeDao.findByName(eventTypeName).orElseThrow(() -> new ResourceNotFoundException(EventTypeMessages.EVENT_TYPE_NOT_FOUND));
        return eventTypeMapper.toDto(result);
    }

    @Override
    public List<EventTypeDto> getAllEventTypes() {
        var result = eventTypeDao.findAll();
        return eventTypeMapper.toDtoList(result);
    }

    @Override
    public EventTypeDto addEventType(CreateEventTypeRequest createEventTypeRequest) {
        Set<String> roles = createEventTypeRequest.getAuthorizedRoles() != null ? createEventTypeRequest.getAuthorizedRoles() : Set.of();

        var eventType = EventType.builder()
                .name(createEventTypeRequest.getName())
                .authorizedRoles(roles)
                .build();

        return eventTypeMapper.toDto(eventTypeDao.save(eventType));
    }

    @Override
    public EventTypeDto updateEventType(UUID id, UpdateEventTypeRequest updateEventTypeRequest) {
        var eventType = getEventTypeEntityById(id);

        if(updateEventTypeRequest.getName() != null && !updateEventTypeRequest.getName().isEmpty()) {
            eventType.setName(updateEventTypeRequest.getName());
        }

        if (updateEventTypeRequest.getAuthorizedRoles() != null) {
            eventType.setAuthorizedRoles(updateEventTypeRequest.getAuthorizedRoles());
        }

        return eventTypeMapper.toDto(eventTypeDao.save(eventType));
    }

    @Override
    public void deleteEventType(UUID id) {
        var eventType = getEventTypeEntityById(id);

        eventTypeDao.delete(eventType);
    }

    @Override
    public EventType getEventTypeEntityById(UUID eventTypeId) {
        return eventTypeDao.findById(eventTypeId).orElseThrow(() -> new ResourceNotFoundException(EventTypeMessages.EVENT_TYPE_NOT_FOUND));
    }

    @Override
    public EventType getEventTypeEntityByName(String eventTypeName) {
        if (eventTypeName == null || eventTypeName.isEmpty()) {
            throw new ValidationException(EventTypeMessages.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK);
        }

        return eventTypeDao.findByName(eventTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(EventTypeMessages.EVENT_TYPE_NOT_FOUND));
    }

}