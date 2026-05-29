package com.skylab.superapp.core.security.opa;

import com.skylab.superapp.core.properties.OpaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

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

}