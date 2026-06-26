package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * EVENT kaynagi icin baglam cozer (DAO ile; EventManager'a bagimli degil -> circular dep yok).
 *   CREATE        : key = CreateEventRequest -> ownerTeam
 *   UPDATE/DELETE : key = UUID eventId       -> Event -> getOwnerTeam()
 */
@Component
@RequiredArgsConstructor
public class EventResourceContextResolver implements ResourceContextResolver {

    private final EventDao eventDao;

    @Override
    public String resourceType() {
        return "EVENT";
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
        if (key instanceof CreateEventRequest request) {
            return request.getOwnerTeam();
        }
        if (key instanceof UUID eventId) {
            return eventDao.findById(eventId)
                    .map(Event::getOwnerTeam)
                    .orElse(null);
        }
        return null;
    }
}
