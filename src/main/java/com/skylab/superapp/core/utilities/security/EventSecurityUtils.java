package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.entities.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventSecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(EventSecurityUtils.class);

    private static final Set<String> PRIVILEGED_ROLES = Set.of("ADMIN", "YK", "DK");

    public void checkAuthorization(EventType eventType) {
        logger.debug("Checking authorization for event type: {}", eventType.getName());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Authorization failed: No active authentication found in SecurityContext.");
            throw new AccessDeniedException("Kullanıcı girişi yapılmamış!");
        }

        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toSet());

        boolean isAuthorized = userRoles.stream()
                .anyMatch(role -> PRIVILEGED_ROLES.contains(role) || eventType.getAuthorizedRoles().contains(role));

        if (!isAuthorized) {
            logger.error("Authorization failed. Required roles: {} | User roles: {}", eventType.getAuthorizedRoles(), userRoles);
            throw new AccessDeniedException(EventMessages.USER_NOT_AUTHORIZED_FOR_EVENT_TYPE);
        }

        logger.debug("Authorization successful for event type: {}", eventType.getName());
    }
}