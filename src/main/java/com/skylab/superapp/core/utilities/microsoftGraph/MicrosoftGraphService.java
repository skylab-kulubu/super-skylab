package com.skylab.superapp.core.utilities.microsoftGraph;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MicrosoftGraphService {

    private final RestTemplate restTemplate;

    public MicrosoftGraphService() {
        this.restTemplate = new RestTemplate();
    }

    public String fetchUserDepartment(String microsoftAccessToken) {
        log.debug("Initiating department fetch from Microsoft Graph API.");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(microsoftAccessToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://graph.microsoft.com/v1.0/me?$select=department",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("department")) {
                String department = (String) response.getBody().get("department");
                log.info("Department fetched successfully from Microsoft Graph API. Department: {}", department);
                return department;
            }

        } catch (Exception e) {
            log.error("Microsoft Graph API fetch failed: Unexpected error. ErrorMessage: {}", e.getMessage(), e);
        }

        return null;
    }
}