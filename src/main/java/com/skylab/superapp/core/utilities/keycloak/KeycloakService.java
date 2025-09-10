package com.skylab.superapp.core.utilities.keycloak;

import com.skylab.superapp.core.exceptions.KeycloakException;
import com.skylab.superapp.core.utilities.keycloak.dtos.UserKeycloakRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    public String createUser(UserKeycloakRequest userRequest) {
        logger.info("Creating user in Keycloak");
        String userId = createUserInKeycloak(userRequest);
        logger.info("User successfully created in Keycloak: userId={}", userId);
        logger.info("Assigning user role");
        assignRealmRole(userId, "user");
        logger.info("User successfully created and role assigned: userId={}", userId);
        return userId;
    }

    public void updateUser(String keycloakUserId, UserKeycloakRequest userRequest) {
        String token = getAdminAccessToken();
        String url = keycloakBaseUrl + "/admin/realms/{realm}/users/{userId}";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> updatePayload = Map.of(
                "firstName", userRequest.getFirstName(),
                "lastName", userRequest.getLastName()
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updatePayload, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class, realm, keycloakUserId);
            logger.info("User updated successfully in Keycloak: userId={}", keycloakUserId);
        } catch (HttpClientErrorException e) {
            logger.error("Failed to update user in Keycloak: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException("User update failed: " + e.getResponseBodyAsString());
        }
    }

    public void deleteUser(String keyCloakUserId) {
        String token = getAdminAccessToken();
        String url = keycloakBaseUrl + "/admin/realms/{realm}/users/{userId}";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class, realm, keyCloakUserId);
            logger.info("User successfully deleted from Keycloak: userId={}", keyCloakUserId);
        } catch (HttpClientErrorException e) {
            logger.error("Failed to delete user from keycloak: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException("User delete failed: " + e.getResponseBodyAsString());
        }
    }

    private String createUserInKeycloak(UserKeycloakRequest userRequest) {
        String token = getAdminAccessToken();
        String url = keycloakBaseUrl + "/admin/realms/{realm}/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> userPayload = Map.of(
                "username", userRequest.getUsername(),
                "email", userRequest.getEmail(),
                "firstName", userRequest.getFirstName(),
                "lastName", userRequest.getLastName(),
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", userRequest.getPassword(),
                                "temporary", false
                        )
                )
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(userPayload, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, requestEntity, Void.class, realm);
            URI location = response.getHeaders().getLocation();
            if (location != null) {
                String path = location.getPath();
                String userId = path.substring(path.lastIndexOf('/') + 1);
                logger.debug("User created in Keycloak. ID: {}", userId);
                return userId;
            } else {
                logger.error("User created but 'Location' header is missing.");
                throw new KeycloakException("User created but 'Location' header is missing.");
            }
        } catch (HttpClientErrorException e) {
            logger.error("Failed to create user in Keycloak: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException("User creation failed: " + e.getResponseBodyAsString());
        }
    }

    public void assignRealmRole(String userId, String roleName) {
        String token = getAdminAccessToken();
        Map<String, Object> role = getRoleByName(roleName, token);

        String url = keycloakBaseUrl + "/admin/realms/{realm}/users/{userId}/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(Collections.singletonList(role), headers);

        try {
            restTemplate.postForEntity(url, requestEntity, Void.class, realm, userId);
            logger.debug("Role '{}' successfully assigned to user '{}'", roleName, userId);
        } catch (HttpClientErrorException e) {
            logger.error("Failed to assign role '{}' to user '{}': {}", roleName, userId, e.getResponseBodyAsString(), e);
            throw new KeycloakException("Role assignment failed: " + e.getResponseBodyAsString());
        }
    }

    public List<String> getUserRoles(String userId) {
        String token = getAdminAccessToken();
        String url = keycloakBaseUrl + "/admin/realms/{realm}/users/{userId}/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    List.class,
                    realm,
                    userId
            );

            List<Map<String, Object>> roles = response.getBody();
            if (roles == null) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .map(role -> (String) role.get("name"))
                    .toList();

        } catch (HttpClientErrorException e) {
            logger.error("Failed to fetch user roles from Keycloak: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException("User roles fetch failed: " + e.getResponseBodyAsString());
        }
    }

    private Map<String, Object> getRoleByName(String roleName, String token) {
        String url = keycloakBaseUrl + "/admin/realms/{realm}/roles/{roleName}";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class, realm, roleName);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("Failed to fetch role '{}' from Keycloak: {}", roleName, e.getResponseBodyAsString(), e);
            throw new KeycloakException("Role fetch failed: " + e.getResponseBodyAsString());
        }
    }


    private String getAdminAccessToken() {
        String url = keycloakBaseUrl + "/realms/{realm}/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class, realm);
            String token = (String) Objects.requireNonNull(response.getBody()).get("access_token");

            if (token == null) {
                logger.error("Admin token is missing from Keycloak response.");
                throw new KeycloakException("Access token not found in response.");
            }
            logger.debug("Admin access token successfully obtained.");
            return token;
        } catch (HttpClientErrorException e) {
            logger.error("HttpClient error while obtaining admin token: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException("Admin token request failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error while obtaining admin token", e);
            throw new KeycloakException("Unexpected error while obtaining admin token");
        }
    }
}