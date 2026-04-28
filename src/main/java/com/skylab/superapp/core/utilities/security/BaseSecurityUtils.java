package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import com.skylab.superapp.core.security.opa.OpaInput;
import com.skylab.superapp.core.security.opa.OpaResource;
import com.skylab.superapp.core.security.opa.OpaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseSecurityUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OpaClient opaClient;

    protected BaseSecurityUtils(OpaClient opaClient) {
        this.opaClient = opaClient;
    }


    protected void checkPermission(String resourceType, String action, String eventTypeName) {
        logger.debug("OPA sorgusu başlatıldı - Kaynak: {}, Aksiyon: {}, EventType: {}", resourceType, action, eventTypeName);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Kullanıcı girişi yapılmamış!");
        }

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toList());

        OpaInput input = OpaInput.builder()
                .user(OpaUser.builder()
                        .id(auth.getName())
                        .roles(roles)
                        .build())
                .resource(OpaResource.builder()
                        .type(resourceType)
                        .eventType(eventTypeName)
                        .build())
                .action(action)
                .build();

        if (!opaClient.isAllowed(input)) {
            logger.error("OPA REDDETTİ! Kaynak: {} Aksiyon: {} Kullanıcı: {} Roller: {}",
                    resourceType, action, auth.getName(), roles);
            throw new AccessDeniedException("Bu işlem için yetkiniz bulunmamaktadır.");
        }

        logger.debug("OPA ONAYLADI! Kaynak: {} Aksiyon: {}", resourceType, action);
    }
}