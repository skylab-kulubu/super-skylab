package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventDayService;
import com.skylab.superapp.core.constants.EventDayMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventDayMapper;
import com.skylab.superapp.dataAccess.EventDayDao;
import com.skylab.superapp.entities.DTOs.eventDay.GetEventDayResponseDto;
import com.skylab.superapp.entities.EventDay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDayManager implements EventDayService {

    private final EventDayDao eventDayDao;
    private final EventDayMapper eventDayMapper;

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
}