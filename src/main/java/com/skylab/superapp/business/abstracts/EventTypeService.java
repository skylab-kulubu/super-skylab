package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;

public interface EventTypeService {
    EventType getEventTypeByName(String eventTypeName);

    EventType getEventTypeById(int eventTypeId);


}