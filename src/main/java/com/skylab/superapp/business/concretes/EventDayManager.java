package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventDayService;
import com.skylab.superapp.core.constants.EventDayMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.EventDayMapper;
import com.skylab.superapp.dataAccess.EventDayDao;
import com.skylab.superapp.entities.DTOs.eventDay.GetEventDayResponseDto;
import com.skylab.superapp.entities.EventDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventDayManager implements EventDayService {

    private final EventDayDao eventDayDao;

    private final EventDayMapper eventDayMapper;

    private final Logger logger = LoggerFactory.getLogger(EventDayManager.class);


    public EventDayManager(EventDayDao eventDayDao, EventDayMapper eventDayMapper) {
        this.eventDayDao = eventDayDao;
        this.eventDayMapper = eventDayMapper;
    }

    @Override
    public EventDay getEventDayReference(UUID eventDayId) {
        logger.info("Fetching event day reference for ID: {}", eventDayId);
        return eventDayDao.getReferenceById(eventDayId);

    }

    @Override
    public GetEventDayResponseDto getEventDayById(UUID eventDayId) {
        logger.info("Fetching event day details for ID: {}", eventDayId);

        EventDay eventDay = eventDayDao.findById(eventDayId)
                .orElseThrow(() -> {
                    logger.error("Event day not found with ID: {}", eventDayId);
                    return new ResourceNotFoundException(EventDayMessages.EVENT_DAY_NOT_FOUND_WITH_ID);
                });

        return eventDayMapper.eventDayToGetEventDayResponseDto(eventDay);

    }

    @Override
    public EventDay getEventDayEntityById(UUID eventDayId) {
        logger.info("Fetching event day entity for ID: {}", eventDayId);

        EventDay eventDay = eventDayDao.findById(eventDayId).orElseThrow(()-> {
            logger.error("Event day not found with ID: {}", eventDayId);
            return new ResourceNotFoundException(EventDayMessages.EVENT_DAY_NOT_FOUND_WITH_ID);
        });

        return eventDay;

    }
}
