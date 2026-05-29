package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.dataAccess.EventDayDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventDay;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.DTOs.eventDay.CreateEventDayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * EVENT_DAY -> sahip etkinligin turu.
 *   CREATE        : CreateEventDayRequest -> eventId -> Event -> type
 *   UPDATE/DELETE : UUID eventDayId       -> EventDay -> getEvent() -> type
 */
@Component
@RequiredArgsConstructor
public class EventDayResourceContextResolver implements ResourceContextResolver {

    private final EventDayDao eventDayDao;
    private final EventDao eventDao;

    @Override
    public String resourceType() {
        return "EVENT_DAY";
    }

    @Override
    public ResourceContext resolve(String action, Object key) {
        EventType type = resolveType(key);
        if (type == null) {
            return ResourceContext.empty();
        }
        return ResourceContext.builder()
                .eventType(type.getName())
                .ownerGroup(type.getOwnerGroup())
                .build();
    }

    private EventType resolveType(Object key) {
        if (key instanceof CreateEventDayRequest request) {
            return eventDao.findById(request.getEventId()).map(Event::getType).orElse(null);
        }
        if (key instanceof UUID id) {
            return eventDayDao.findById(id).map(EventDay::getEvent).map(Event::getType).orElse(null);
        }
        return null;
    }
}
