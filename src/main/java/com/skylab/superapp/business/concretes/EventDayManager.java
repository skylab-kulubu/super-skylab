package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventDayService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventDayMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.EventDayMapper;
import com.skylab.superapp.core.utilities.security.EventDaySecurityUtils;
import com.skylab.superapp.dataAccess.EventDayDao;
import com.skylab.superapp.entities.DTOs.eventDay.CreateEventDayRequest;
import com.skylab.superapp.entities.DTOs.eventDay.GetEventDayResponseDto;
import com.skylab.superapp.entities.DTOs.eventDay.UpdateEventDayRequest;
import com.skylab.superapp.entities.EventDay;
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
public class EventDayManager implements EventDayService {

    private final EventDayDao eventDayDao;
    private final EventDayMapper eventDayMapper;
    private final EventService eventService;
    private final EventDaySecurityUtils eventDaySecurityUtils;

    @Override
    public EventDay getEventDayReference(UUID eventDayId) {
        log.debug("Retrieving event day reference. EventDayId: {}", eventDayId);
        return eventDayDao.getReferenceById(eventDayId);
    }

    @Override
    public GetEventDayResponseDto getEventDayById(UUID eventDayId) {
        log.debug("Retrieving event day details. EventDayId: {}", eventDayId);

        EventDay eventDay = eventDayDao.findById(eventDayId)
                .orElseThrow(() -> {
                    log.error("Event day retrieval failed: Resource not found. EventDayId: {}", eventDayId);
                    return new ResourceNotFoundException(EventDayMessages.EVENT_DAY_NOT_FOUND_WITH_ID);
                });

        return eventDayMapper.eventDayToGetEventDayResponseDto(eventDay);
    }

    @Override
    public EventDay getEventDayEntityById(UUID eventDayId) {
        log.debug("Retrieving event day entity. EventDayId: {}", eventDayId);

        return eventDayDao.findById(eventDayId).orElseThrow(() -> {
            log.error("Event day entity retrieval failed: Resource not found. EventDayId: {}", eventDayId);
            return new ResourceNotFoundException(EventDayMessages.EVENT_DAY_NOT_FOUND_WITH_ID);
        });
    }

    @Override
    @Transactional
    public GetEventDayResponseDto createEventDay(CreateEventDayRequest request) {
        log.info("Initiating event day creation. EventId: {}", request.getEventId());

        var event = eventService.getEventEntityById(request.getEventId());
        eventDaySecurityUtils.checkCreate(event.getType().getName());

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationException(EventDayMessages.EVENT_DAY_START_DATE_AFTER_END_DATE);
        }


        EventDay eventDay = new EventDay();
        eventDay.setEvent(event);
        eventDay.setStartDate(request.getStartDate());
        eventDay.setEndDate(request.getEndDate());

        var saved = eventDayDao.save(eventDay);
        log.info("Event day created successfully. EventDayId: {}", saved.getId());

        return eventDayMapper.eventDayToGetEventDayResponseDto(saved);

    }

    @Override
    @Transactional
    public GetEventDayResponseDto updateEventDay(UUID id, UpdateEventDayRequest request) {
        log.info("Initiating event day update. EventDayId: {}", id);

        var eventDay = getEventDayEntityById(id);
        eventDaySecurityUtils.checkUpdate(eventDay.getEvent().getType().getName());

        var newStart = request.getStartDate() != null ? request.getStartDate() : eventDay.getStartDate();
        var newEnd = request.getEndDate() != null ? request.getEndDate() : eventDay.getEndDate();

        if (newStart != null && newEnd != null && newStart.isAfter(newEnd)) {
            throw new ValidationException(EventDayMessages.EVENT_DAY_START_DATE_AFTER_END_DATE);
        }

        if (request.getStartDate() != null) eventDay.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) eventDay.setEndDate(request.getEndDate());

        var saved = eventDayDao.save(eventDay);
        log.info("Event day updated successfully. EventDayId: {}", id);

        return eventDayMapper.eventDayToGetEventDayResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteEventDay(UUID id) {
        log.info("Initiating event day deletion. EventDayId: {}", id);

        var eventDay = getEventDayEntityById(id);
        eventDaySecurityUtils.checkDelete(eventDay.getEvent().getType().getName());

        if (eventDay.getSessions() != null && !eventDay.getSessions().isEmpty()) {
            log.warn("Event day deletion failed: Has sessions. EventDayId: {}", id);
            throw new BusinessException(EventDayMessages.EVENT_DAY_HAS_SESSIONS);
        }

        eventDayDao.delete(eventDay);
        log.info("Event day deleted successfully. EventDayId: {}", id);
    }

    @Override
    public List<GetEventDayResponseDto> getEventDaysByEventId(UUID eventId) {
        log.debug("Retrieving event days by event. EventId: {}", eventId);
        eventDaySecurityUtils.checkRead();

        var event = eventService.getEventEntityById(eventId);
        var eventDays = eventDayDao.findAllByEvent(event);

        log.info("Event days retrieved. EventId: {}, TotalCount: {}", eventId, eventDays.size());

        return eventDays.stream()
                .map(eventDayMapper::eventDayToGetEventDayResponseDto)
                .collect(Collectors.toList());
    }

}