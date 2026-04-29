package com.skylab.superapp.core.security.opa;

import com.skylab.superapp.core.properties.OpaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpaClient {

    private final WebClient webClient;
    private final OpaProperties opaProperties;

    public boolean isAllowed(OpaInput input) {
        log.debug("Initiating OPA authorization check. Resource: {}, Action: {}",
                input.getResource().getType(), input.getAction());

        try {
            OpaResponse response = webClient.post()
                    .uri(opaProperties.getUrl() + "/v1/data/skylab/authz/allow")
                    .bodyValue(Map.of("input", input))
                    .retrieve()
                    .bodyToMono(OpaResponse.class)
                    .block();

            boolean isAllowed = response != null && Boolean.TRUE.equals(response.getResult());
            log.debug("OPA authorization check completed. IsAllowed: {}", isAllowed);

            return isAllowed;

        } catch (Exception e) {
            log.error("OPA service connection failed during authorization check. ErrorMessage: {}", e.getMessage(), e);
            throw new AccessDeniedException("opa.service.unavailable");
        }
    }

    public boolean isValidEventType(String eventTypeName) {
        log.debug("Initiating OPA event type validation. EventTypeName: {}", eventTypeName);

        try {
            OpaResponse response = webClient.post()
                    .uri(opaProperties.getUrl() + "/v1/data/skylab/event_types/is_valid")
                    .bodyValue(Map.of("input", Map.of("name", eventTypeName)))
                    .retrieve()
                    .bodyToMono(OpaResponse.class)
                    .block();

            boolean isValid = response != null && Boolean.TRUE.equals(response.getResult());
            log.debug("OPA event type validation completed. EventTypeName: {}, IsValid: {}", eventTypeName, isValid);

            return isValid;

        } catch (Exception e) {
            log.error("OPA service connection failed during event type validation. EventTypeName: {}, ErrorMessage: {}", eventTypeName, e.getMessage(), e);
            throw new AccessDeniedException("opa.service.unavailable");
        }
    }

    public Set<String> getRolesForEventType(String eventTypeName) {
        log.debug("Initiating OPA role retrieval for event type. EventTypeName: {}", eventTypeName);

        try {
            var response = webClient.get()
                    .uri(opaProperties.getUrl() + "/v1/data/skylab/event_type_roles/" + eventTypeName)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("result") == null) {
                log.debug("OPA role retrieval completed: No roles found. EventTypeName: {}", eventTypeName);
                return Set.of();
            }

            List<String> roles = (List<String>) response.get("result");
            log.info("OPA roles retrieved successfully. EventTypeName: {}, RoleCount: {}", eventTypeName, roles.size());

            return new HashSet<>(roles);

        } catch (Exception e) {
            log.error("OPA service connection failed during role retrieval. EventTypeName: {}, ErrorMessage: {}", eventTypeName, e.getMessage(), e);
            return Set.of();
        }
    }
}