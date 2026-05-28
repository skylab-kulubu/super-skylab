package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * COMPETITOR -> sahip etkinligin turu.
 *   CREATE        : CreateCompetitorRequest -> eventId -> Event -> type
 *   UPDATE/DELETE : UUID competitorId       -> Competitor -> getEvent() -> type
 */
@Component
@RequiredArgsConstructor
public class CompetitorResourceContextResolver implements ResourceContextResolver {

    private final CompetitorDao competitorDao;
    private final EventDao eventDao;

    @Override
    public String resourceType() {
        return "COMPETITOR";
    }

    @Override
    public ResourceContext resolve(String action, Object key) {
        // CREATE: hedef kullanici = request.userId (self-registration kontrolu icin)
        if (key instanceof CreateCompetitorRequest request) {
            EventType type = eventDao.findById(request.getEventId()).map(Event::getType).orElse(null);
            return build(type, request.getUserId());
        }
        // UPDATE/DELETE: kaynak sahibi = competitor.user
        if (key instanceof UUID id) {
            Competitor competitor = competitorDao.findById(id).orElse(null);
            if (competitor == null) {
                return ResourceContext.empty();
            }
            EventType type = competitor.getEvent() != null ? competitor.getEvent().getType() : null;
            UUID ownerUserId = competitor.getUser() != null ? competitor.getUser().getId() : null;
            return build(type, ownerUserId);
        }
        return ResourceContext.empty();
    }

    private ResourceContext build(EventType type, UUID ownerId) {
        return ResourceContext.builder()
                .eventType(type != null ? type.getName() : null)
                .ownerGroup(type != null ? type.getOwnerGroup() : null)
                .ownerId(ownerId != null ? ownerId.toString() : null)
                .build();
    }
}
