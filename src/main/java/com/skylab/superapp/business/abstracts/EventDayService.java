package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.eventDay.CreateEventDayRequest;
import com.skylab.superapp.entities.DTOs.eventDay.GetEventDayResponseDto;
import com.skylab.superapp.entities.DTOs.eventDay.UpdateEventDayRequest;
import com.skylab.superapp.entities.EventDay;

import java.util.List;
import java.util.UUID;

public interface EventDayService {
    EventDay getEventDayReference(UUID eventDayId);

    GetEventDayResponseDto getEventDayById(UUID eventDayId);

    EventDay getEventDayEntityById(UUID eventDayId);

    GetEventDayResponseDto createEventDay(CreateEventDayRequest request);
    GetEventDayResponseDto updateEventDay(UUID id, UpdateEventDayRequest request);
    void deleteEventDay(UUID id);
    List<GetEventDayResponseDto> getEventDaysByEventId(UUID eventId);

}
