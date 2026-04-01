package com.skylab.superapp.core.utilities.microsoftGraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MicrosoftGraphService {

    private final Logger logger = LoggerFactory.getLogger(MicrosoftGraphService.class);
    private final RestTemplate restTemplate;

    public MicrosoftGraphService() {
        this.restTemplate = new RestTemplate();
    }

    public String fetchUserDepartment(String microsoftAccessToken) {
        logger.info("Microsoft Graph API'den bölüm bilgisi çekiliyor...");

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
                logger.info("Graph API'den bölüm başarıyla alındı: {}", department);
                return department;
            }

        } catch (Exception e) {
            logger.error("Microsoft Graph API'den bölüm çekilirken hata oluştu: {}", e.getMessage());
        }

        return null;
    }

}