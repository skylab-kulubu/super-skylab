package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.EventDayDao;
import com.skylab.superapp.dataAccess.SessionDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventDay;
import com.skylab.superapp.entities.Session;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * SESSION -> sahip etkinligin takimi (session -> eventDay -> event -> ownerTeam).
 *   CREATE        : CreateSessionRequest -> eventDayId -> EventDay -> getEvent() -> ownerTeam
 *   UPDATE/DELETE : UUID sessionId       -> Session -> getEventDay() -> getEvent() -> ownerTeam
 */
@Component
@RequiredArgsConstructor
public class SessionResourceContextResolver implements ResourceContextResolver {

    private final SessionDao sessionDao;
    private final EventDayDao eventDayDao;

    @Override
    public String resourceType() {
        return "SESSION";
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
        if (key instanceof CreateSessionRequest request) {
            return eventDayDao.findById(request.getEventDayId())
                    .map(EventDay::getEvent).map(Event::getOwnerTeam).orElse(null);
        }
        if (key instanceof UUID id) {
            return sessionDao.findById(id)
                    .map(Session::getEventDay).map(EventDay::getEvent).map(Event::getOwnerTeam).orElse(null);
        }
        return null;
    }
}
