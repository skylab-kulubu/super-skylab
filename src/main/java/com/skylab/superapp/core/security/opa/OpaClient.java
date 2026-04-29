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
        try {
            OpaResponse response = webClient.post()
                    .uri(opaProperties.getUrl() + "/v1/data/skylab/authz/allow")
                    .bodyValue(Map.of("input", input))
                    .retrieve()
                    .bodyToMono(OpaResponse.class)
                    .block();


            return response != null && Boolean.TRUE.equals(response.getResult());


        } catch (Exception e) {
            throw new AccessDeniedException("OPA Servisine erişilemedi!");
        }


    }


    public boolean isValidEventType(String eventTypeName) {
        try {
            OpaResponse response = webClient.post()
                    .uri(opaProperties.getUrl() + "/v1/data/skylab/event_types/is_valid")
                    .bodyValue(Map.of("input", Map.of("name", eventTypeName)))
                    .retrieve()
                    .bodyToMono(OpaResponse.class)
                    .block();

            return response != null && Boolean.TRUE.equals(response.getResult());
        } catch (Exception e) {
            throw new AccessDeniedException("OPA Servisine erişilemedi!.");
        }
    }


    public Set<String> getRolesForEventType(String eventTypeName) {
        try {
            var response = webClient.get()
                    .uri(opaProperties.getUrl() + "/v1/data/skylab/event_type_roles/" + eventTypeName)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("result") == null) {
                return Set.of();
            }

            List<String> roles = (List<String>) response.get("result");
            return new HashSet<>(roles);

        } catch (Exception e) {
            log.warn("OPA'dan rol listesi alınamadı: {}", eventTypeName);
            return Set.of();
        }
    }
}
