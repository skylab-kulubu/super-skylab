package com.skylab.superapp.core.utilities.mail.skymail;

import com.skylab.superapp.core.properties.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkyMailTokenProvider {

    private final RestTemplate restTemplate;
    private final KeycloakProperties keycloakProperties;

    private String cachedToken;
    private Instant tokenExpiry;

    public synchronized String getToken() {
        if (cachedToken == null || Instant.now().isAfter(tokenExpiry.minusSeconds(30))) {
            refreshToken();
        }
        return cachedToken;
    }

    private void refreshToken() {
        log.debug("SkyMail: Refreshing access token.");

        String tokenUrl = keycloakProperties.getExternalUrl()
                + "/realms/" + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", keycloakProperties.getClientId());
        form.add("client_secret", keycloakProperties.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map response = restTemplate.postForObject(
                tokenUrl,
                new HttpEntity<>(form, headers),
                Map.class
        );

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("SkyMail: Token response is invalid or missing access_token.");
        }

        cachedToken = (String) response.get("access_token");
        int expiresIn = (Integer) response.get("expires_in");
        tokenExpiry = Instant.now().plusSeconds(expiresIn);

        log.info("SkyMail: Access token refreshed. ExpiresIn: {}s", expiresIn);
    }
}
