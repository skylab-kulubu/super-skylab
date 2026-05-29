package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.TicketDao;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * TICKET -> sahip etkinligin turu (ticket -> event -> type).
 * VALIDATE/UPDATE/DELETE: UUID ticketId -> Ticket -> getEvent() -> type.
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
            EventType type = ticketDao.findById(id).map(Ticket::getEvent).map(Event::getType).orElse(null);
            if (type != null) {
                return ResourceContext.builder()
                        .eventType(type.getName())
                        .ownerGroup(type.getOwnerGroup())
                        .build();
            }
        }
        return ResourceContext.empty();
    }
}
