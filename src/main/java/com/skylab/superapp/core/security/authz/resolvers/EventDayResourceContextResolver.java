package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.dataAccess.EventDayDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventDay;
import com.skylab.superapp.entities.DTOs.eventDay.CreateEventDayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * EVENT_DAY -> sahip etkinligin takimi.
 *   CREATE        : CreateEventDayRequest -> eventId -> Event -> ownerTeam
 *   UPDATE/DELETE : UUID eventDayId       -> EventDay -> getEvent() -> ownerTeam
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
        String team = resolveOwnerTeam(key);
        if (team == null) {
            return ResourceContext.empty();
        }
        return ResourceContext.builder()
                .eventType(team)
                .ownerGroup(team)
                .build();
    }

    private String resolveOwnerTeam(Object key) {
        if (key instanceof CreateEventDayRequest request) {
            return eventDao.findById(request.getEventId()).map(Event::getOwnerTeam).orElse(null);
        }
        if (key instanceof UUID id) {
            return eventDayDao.findById(id).map(EventDay::getEvent).map(Event::getOwnerTeam).orElse(null);
        }
        return null;
    }
}
