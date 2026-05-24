package com.skylab.superapp.core.rabbit.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.identity.keycloak.KeycloakAdminService;
import com.skylab.superapp.core.properties.KeycloakProperties;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(KeycloakUserEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;

    public KeycloakUserEventConsumer(ObjectMapper objectMapper, KeycloakProperties keycloakProperties, KeycloakAdminService keycloakAdminService, UserService userService) {
        this.objectMapper = objectMapper;
        this.keycloakProperties = keycloakProperties;
        this.keycloakAdminService = keycloakAdminService;
        this.userService = userService;
    }

    @RabbitListener(queues = "skylab.user.events", containerFactory = "keycloakListenerContainerFactory")
    public void handleKeycloakUserEvent(Message message) {
        try {
            String messagePayload = new String(message.getBody());
            JsonNode rootNode = objectMapper.readTree(messagePayload);

            String eventClass = rootNode.path("@class").asText("");
            boolean isAdminEvent = eventClass.endsWith("EventAdminNotificationMqMsg");
            boolean isClientEvent = eventClass.endsWith("EventClientNotificationMqMsg");

            String userId = null;
            String clientId = null;

            if (isAdminEvent) {
                String resourceType = rootNode.path("resourceType").asText("");
                String operationType = rootNode.path("operationType").asText("");

                if (!"USER".equals(resourceType)) {
                    return;
                }

                if (!"CREATE".equals(operationType) && !"UPDATE".equals(operationType)) {
                    return;
                }

                clientId = rootNode.path("authDetails").path("clientId").asText();
                String resourcePath = rootNode.path("resourcePath").asText("");
                if (resourcePath.startsWith("users/")) {
                    userId = resourcePath.replace("users/", "");
                }

            } else if (isClientEvent) {
                String type = rootNode.path("type").asText("");


                if (!"REGISTER".equals(type) && !"UPDATE_PROFILE".equals(type) && !"UPDATE_EMAIL".equals(type)) {
                    return;
                }

                clientId = rootNode.path("clientId").asText();
                userId = rootNode.path("userId").asText(null);
            } else {
                return;
            }

            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("UserId could not be extracted from Keycloak event!");
                return;
            }

            if (keycloakProperties.getClientId().equals(clientId)) {
                return;
            }

            UserRepresentation keycloakUser = keycloakAdminService.getUserById(userId);
            if (keycloakUser != null) {
                userService.syncUserFromKeycloak(userId, keycloakUser);
                logger.info("User {} synced successfully from Keycloak (Triggered by {} event).", userId, isAdminEvent ? "Admin" : "Client");
            }

        } catch (Exception e) {
        logger.error("Error processing Keycloak user event: {}", e.getMessage());
        throw new RuntimeException("Keycloak user event could not be processed, retrying...", e);
    }
    }
}