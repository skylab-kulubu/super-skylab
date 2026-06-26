package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * COMPETITOR -> sahip etkinligin takimi.
 *   CREATE        : CreateCompetitorRequest -> eventId -> Event -> ownerTeam
 *   UPDATE/DELETE : UUID competitorId       -> Competitor -> getEvent() -> ownerTeam
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
        if (key instanceof CreateCompetitorRequest request) {
            String team = request.getEventId() == null ? null
                    : eventDao.findById(request.getEventId()).map(Event::getOwnerTeam).orElse(null);
            return build(team, request.getUserId());
        }
        // UPDATE/DELETE: kaynak sahibi = competitor.user
        if (key instanceof UUID id) {
            Competitor competitor = competitorDao.findById(id).orElse(null);
            if (competitor == null) {
                return ResourceContext.empty();
            }
            String team = competitor.getEvent() != null ? competitor.getEvent().getOwnerTeam() : null;
            UUID ownerUserId = competitor.getUser() != null ? competitor.getUser().getId() : null;
            return build(team, ownerUserId);
        }
        return ResourceContext.empty();
    }

    private ResourceContext build(String team, UUID ownerId) {
        return ResourceContext.builder()
                .eventType(team)
                .ownerGroup(team)
                .ownerId(ownerId != null ? ownerId.toString() : null)
                .build();
    }
}
