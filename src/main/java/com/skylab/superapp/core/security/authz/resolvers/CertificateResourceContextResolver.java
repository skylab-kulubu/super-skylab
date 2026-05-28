package com.skylab.superapp.core.security.authz.resolvers;

import com.skylab.superapp.core.security.authz.ResourceContext;
import com.skylab.superapp.core.security.authz.ResourceContextResolver;
import com.skylab.superapp.dataAccess.CertificateDao;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.Certificate;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.DTOs.certificate.CreateCertificateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * CERTIFICATE -> sahip etkinligin turu.
 *   CREATE        : CreateCertificateRequest -> eventId -> Event -> type
 *   UPDATE/DELETE : UUID certificateId       -> Certificate -> getEvent() -> type
 */
@Component
@RequiredArgsConstructor
public class CertificateResourceContextResolver implements ResourceContextResolver {

    private final CertificateDao certificateDao;
    private final EventDao eventDao;

    @Override
    public String resourceType() {
        return "CERTIFICATE";
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
        if (key instanceof CreateCertificateRequest request) {
            return eventDao.findById(request.getEventId()).map(Event::getType).orElse(null);
        }
        if (key instanceof UUID id) {
            return certificateDao.findById(id).map(Certificate::getEvent).map(Event::getType).orElse(null);
        }
        return null;
    }
}
