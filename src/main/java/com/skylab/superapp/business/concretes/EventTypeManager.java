package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.EventTypeMapper;
import com.skylab.superapp.core.utilities.ldap.LdapService;
import com.skylab.superapp.dataAccess.EventTypeDao;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.LdapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventTypeManager implements EventTypeService {

    private final EventTypeDao eventTypeDao;
    private final EventTypeMapper eventTypeMapper;

    private final Logger logger = LoggerFactory.getLogger(EventTypeManager.class);
    private final LdapService ldapService;

    @Autowired
    public EventTypeManager(EventTypeDao eventTypeDao,
                            EventTypeMapper eventTypeMapper, LdapService ldapService) {
        this.eventTypeDao = eventTypeDao;
        this.eventTypeMapper = eventTypeMapper;
        this.ldapService = ldapService;
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

    @Override
    public Set<UserDto> getCoordinatorsByEventTypeName(String eventTypeName) {
        logger.info("Getting coordinators by event type name: {}", eventTypeName);

        EventType eventType = getEventTypeEntityByName(eventTypeName);

        Set<String> roles = eventType.getAuthorizedRoles();

        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        Set<UserDto> coordinators = new HashSet<>();

        for (String role : roles) {
            List<LdapUser> ldapUsers = ldapService.getUsersByGroupName(role);

            for (LdapUser ldapUser : ldapUsers) {
                coordinators.add(mapLdapUserToDto(ldapUser));
            }
        }

        return coordinators.stream()
                .distinct()
                .collect(Collectors.toSet());

    }

    private UserDto mapLdapUserToDto(LdapUser source) {
        UserDto dto = new UserDto();
        dto.setFirstName(source.getFirstName());
        dto.setLastName(source.getLastName());
        dto.setEmail(source.getEmail());
        return dto;
    }

}