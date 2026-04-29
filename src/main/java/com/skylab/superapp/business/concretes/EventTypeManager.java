package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.EventTypeMapper;
import com.skylab.superapp.core.security.opa.OpaClient;
import com.skylab.superapp.core.utilities.security.EventTypeSecurityUtils;
import com.skylab.superapp.dataAccess.EventTypeDao;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import com.skylab.superapp.entities.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventTypeManager implements EventTypeService {

    private final EventTypeDao eventTypeDao;
    private final EventTypeMapper eventTypeMapper;
    private final UserService userService;
    private final OpaClient opaClient;
    private final EventTypeSecurityUtils eventTypeSecurityUtils;

    @Override
    public EventTypeDto getEventTypeById(UUID eventTypeId) {
        log.debug("Retrieving EventType. EventTypeId: {}", eventTypeId);
        return eventTypeMapper.toDto(getEventTypeEntityById(eventTypeId));
    }

    @Override
    public EventTypeDto getEventTypeByName(String eventTypeName) {
        log.debug("Retrieving EventType by name. EventTypeName: {}", eventTypeName);
        var result = eventTypeDao.findByName(eventTypeName).orElseThrow(() -> {
            log.error("EventType retrieval failed: Resource not found. EventTypeName: {}", eventTypeName);
            return new ResourceNotFoundException(EventTypeMessages.EVENT_TYPE_NOT_FOUND);
        });
        return eventTypeMapper.toDto(result);
    }

    @Override
    public List<EventTypeDto> getAllEventTypes() {
        log.debug("Retrieving all event types.");
        var result = eventTypeDao.findAll();

        log.info("Retrieved all event types successfully. TotalCount: {}", result.size());
        return result.stream()
                .map(eventTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventTypeDto addEventType(CreateEventTypeRequest createEventTypeRequest) {
        log.info("Initiating event type creation. EventTypeName: {}", createEventTypeRequest.getName());
        eventTypeSecurityUtils.checkCreate();

        if (!opaClient.isValidEventType(createEventTypeRequest.getName())) {
            log.warn("Event type creation failed: Name not defined in OPA or e-skylab. EventTypeName: {}", createEventTypeRequest.getName());
            throw new BusinessException(EventTypeMessages.EVENT_TYPE_NOT_DEFINED_IN_OPA);
        }

        var eventType = EventType.builder()
                .name(createEventTypeRequest.getName())
                .build();

        var savedEventType = eventTypeDao.save(eventType);
        log.info("Event type created successfully. EventTypeId: {}", savedEventType.getId());

        return eventTypeMapper.toDto(savedEventType);
    }

    @Override
    public EventTypeDto updateEventType(UUID id, UpdateEventTypeRequest updateEventTypeRequest) {
        log.info("Initiating event type update. EventTypeId: {}", id);
        eventTypeSecurityUtils.checkUpdate();

        var eventType = getEventTypeEntityById(id);

        if (updateEventTypeRequest.getName() != null && !updateEventTypeRequest.getName().isEmpty()) {
            if (!opaClient.isValidEventType(updateEventTypeRequest.getName())) {
                log.warn("Event type update failed: New name not defined in OPA or e-skylab. EventTypeId: {}, RequestedName: {}", id, updateEventTypeRequest.getName());
                throw new BusinessException(EventTypeMessages.EVENT_TYPE_NOT_DEFINED_IN_OPA);
            }
            eventType.setName(updateEventTypeRequest.getName());
        }

        var updatedEventType = eventTypeDao.save(eventType);
        log.info("Event type updated successfully. EventTypeId: {}", updatedEventType.getId());

        return eventTypeMapper.toDto(updatedEventType);
    }

    @Override
    public void deleteEventType(UUID id) {
        log.info("Initiating event type deletion. EventTypeId: {}", id);
        eventTypeSecurityUtils.checkDelete();

        var eventType = getEventTypeEntityById(id);
        eventTypeDao.delete(eventType);

        log.info("Event type deleted successfully. EventTypeId: {}", id);
    }

    @Override
    public EventType getEventTypeEntityById(UUID eventTypeId) {
        log.debug("Retrieving EventType entity. EventTypeId: {}", eventTypeId);
        return eventTypeDao.findById(eventTypeId).orElseThrow(() -> {
            log.error("EventType entity retrieval failed: Resource not found. EventTypeId: {}", eventTypeId);
            return new ResourceNotFoundException(EventTypeMessages.EVENT_TYPE_NOT_FOUND);
        });
    }

    @Override
    public EventType getEventTypeEntityByName(String eventTypeName) {
        log.debug("Retrieving EventType entity by name. EventTypeName: {}", eventTypeName);

        if (eventTypeName == null || eventTypeName.isEmpty()) {
            log.warn("EventType entity retrieval failed: Provided name is null or blank.");
            throw new ValidationException(EventTypeMessages.EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK);
        }

        return eventTypeDao.findByName(eventTypeName)
                .orElseThrow(() -> {
                    log.error("EventType entity retrieval failed: Resource not found. EventTypeName: {}", eventTypeName);
                    return new ResourceNotFoundException(EventTypeMessages.EVENT_TYPE_NOT_FOUND);
                });
    }

    @Override
    public Set<UserDto> getCoordinatorsByEventTypeName(String eventTypeName) {
        log.info("Processing coordinator retrieval for event type. EventTypeName: {}", eventTypeName);

        EventType eventType = getEventTypeEntityByName(eventTypeName);
        Set<String> roles = opaClient.getRolesForEventType(eventTypeName);

        if (roles == null || roles.isEmpty()) {
            log.debug("No coordinator roles found for event type in OPA. EventTypeName: {}", eventTypeName);
            return Collections.emptySet();
        }

        List<UserDto> users = userService.getUsersByRoleNames(roles);
        log.info("Coordinators retrieved successfully. EventTypeName: {}, CoordinatorCount: {}", eventTypeName, users.size());

        return new HashSet<>(users);
    }
}