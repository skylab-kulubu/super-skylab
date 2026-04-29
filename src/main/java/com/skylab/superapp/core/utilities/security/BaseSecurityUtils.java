package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import com.skylab.superapp.core.security.opa.OpaInput;
import com.skylab.superapp.core.security.opa.OpaResource;
import com.skylab.superapp.core.security.opa.OpaUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSecurityUtils {

    private final OpaClient opaClient;

    protected void checkPermission(String resourceType, String action, String eventTypeName) {
        log.debug("Initiating OPA permission check. Resource: {}, Action: {}, EventType: {}", resourceType, action, eventTypeName);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.warn("OPA authorization failed: User is not authenticated. Resource: {}, Action: {}", resourceType, action);
            throw new AccessDeniedException("user.not.authenticated");
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
            log.warn("OPA authorization denied. Resource: {}, Action: {}, User: {}, Roles: {}",
                    resourceType, action, auth.getName(), roles);

            throw new AccessDeniedException("security.access.denied");
        }

        log.debug("OPA authorization granted. Resource: {}, Action: {}, User: {}", resourceType, action, auth.getName());
    }
}