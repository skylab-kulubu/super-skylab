package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetBizbizeEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.Event;

import java.util.List;

public interface EventService {

    DataResult<Integer> addEvent(CreateEventDto createEventDto);

    Result deleteEvent(int id);

    Result updateEvent(GetEventDto getEventDto);

    Result updateBizbizeEvent(GetBizbizeEventDto getBizbizeEventDto);

    DataResult<List<GetEventDto>> getAllEventsByTenant(String tenant);

    DataResult<List<GetBizbizeEventDto>> getAllBizbizeEvents();

    DataResult<Event> getEventEntityById(int id);

    DataResult<List<GetEventDto>> getAllEventsByTenantAndType(String tenant, String type);

    Result addPhotosToEvent(int eventId, List<Integer> photoIds);

    DataResult<List<GetEventDto>> getAllFutureEventsByTenant(String tenant);



}

