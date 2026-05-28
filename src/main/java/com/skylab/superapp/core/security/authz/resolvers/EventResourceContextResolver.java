package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.dataAccess.EventTypeDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * EVENT kaynagi icin baglam cozer (DAO ile; EventManager'a bagimli degil -> circular dep yok).
 *   CREATE        : key = CreateEventRequest -> eventTypeId -> EventType
 *   UPDATE/DELETE : key = UUID eventId       -> Event -> getType()
 */
@Component
@RequiredArgsConstructor
public class EventResourceContextResolver implements ResourceContextResolver {

    private final EventDao eventDao;
    private final EventTypeDao eventTypeDao;

    @Override
    public String resourceType() {
        return "EVENT";
    }

    @Override
    public ResourceContext resolve(String action, Object key) {
        EventType type = resolveEventType(key);
        if (type == null) {
            return ResourceContext.empty();
        }
        return ResourceContext.builder()
                .eventType(type.getName())
                .ownerGroup(type.getOwnerGroup())
                .build();
    }

    private EventType resolveEventType(Object key) {
        if (key instanceof CreateEventRequest request) {
            return eventTypeDao.findById(request.getEventTypeId()).orElse(null);
        }
        if (key instanceof UUID eventId) {
            return eventDao.findById(eventId)
                    .map(Event::getType)
                    .orElse(null);
        }
        return null;
    }
}
