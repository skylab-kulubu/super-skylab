package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.TicketDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * TICKET -> sahip etkinligin takimi (ticket -> event -> ownerTeam).
 * VALIDATE/UPDATE/DELETE: UUID ticketId -> Ticket -> getEvent() -> ownerTeam.
 * READ metotlari (anahtarsiz) -> bos context, rol-bazli rego karar verir.
 */
@Component
@RequiredArgsConstructor
public class TicketResourceContextResolver implements ResourceContextResolver {

    private final TicketDao ticketDao;

    @Override
    public String resourceType() {
        return "TICKET";
    }

    @Override
    public ResourceContext resolve(String action, Object key) {
        if (key instanceof UUID id) {
            String team = ticketDao.findById(id).map(Ticket::getEvent).map(Event::getOwnerTeam).orElse(null);
            if (team != null) {
                return ResourceContext.builder()
                        .eventType(team)
                        .ownerGroup(team)
                        .build();
            }
        }
        return ResourceContext.empty();
    }
}
