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

            String type = rootNode.path("type").asText("");
            String operationType = rootNode.path("operationType").asText("");
            String userId = rootNode.has("userId") ? rootNode.get("userId").asText() :
                    rootNode.path("resourcePath").asText().replace("users/", "");

            if (!"REGISTER".equals(type) && !"UPDATE".equals(operationType) && !"CREATE".equals(operationType)) {
                return;
            }

            String clientId = rootNode.path("authDetails").path("clientId").asText();
            if (keycloakProperties.getClientId().equals(clientId)) {
                return;
            }

            UserRepresentation keycloakUser = keycloakAdminService.getUserById(userId);
            if (keycloakUser != null) {
                userService.syncUserFromKeycloak(userId, keycloakUser);
            }

        } catch (Exception e) {
            logger.error("Error processing Keycloak user event: {}", e.getMessage());
            throw new RuntimeException("An error occurred while processing Keycloak user event!", e);
        }
    }
}
